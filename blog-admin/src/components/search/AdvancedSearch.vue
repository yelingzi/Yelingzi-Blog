<template>
    <div>
        <!-- 已选条件 -->
        <div class="list-search">
            <div v-for="(item, index) in chosen" :key="item.value" class="item">
                <div>
                    <!-- 日期 -->
                    <template v-if="item.type === 'Date'">
                        <span class="text">{{ item.label }}：</span>
                        <el-date-picker v-model="searchForm[item.value]" type="daterange" range-separator="到"
                            start-placeholder="开始" end-placeholder="结束" :disabled-date="disabledFuture"
                            style="width:240px" />
                    </template>

                    <!-- 下拉 -->
                    <template v-else-if="item.type === 'Select'">
                        <span class="text">{{ item.label }}：</span>
                        <el-select v-model="searchForm[item.value]" :placeholder="`选择${item.label}`" style="width:140px"
                            clearable>
                            <el-option v-for="opt in item.opt" :key="opt.value" :label="opt.label" :value="opt.value" />
                        </el-select>
                    </template>

                    <!-- 自动补全（标题 / 用户 / 标签 / 分类）-->
                    <template v-else-if="item.type === 'Autocomplete'">
                        <span class="text">{{ item.label }}：</span>
                        <el-autocomplete v-model="searchForm[item.value]"
                            :fetch-suggestions="(qs: string, cb: any) => item.fn(qs, cb)"
                            :placeholder="`输入${item.label}`" clearable style="width:200px" />
                    </template>
                </div>

                <!-- 删除按钮 -->
                <el-button class="item-btn" type="primary" circle plain @click="remove(index)">
                    <el-icon>
                        <Delete />
                    </el-icon>
                </el-button>
            </div>
        </div>

        <!-- 新增行 -->
        <div class="add">
            <el-select v-model="toAdd" placeholder="新增搜索" style="width:140px" :disabled="rest.length === 0">
                <el-option v-for="r in rest" :key="r.value" :label="r.label" :value="r.value" />
            </el-select>
            <el-button type="primary" :icon="Plus" @click="add">新增</el-button>

            <el-button type="success" @click="emitQuery">查询</el-button>
            <el-button @click="reset">重置</el-button>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, defineEmits, defineProps } from 'vue'
import { Delete, Plus } from '@element-plus/icons-vue'

type Option =
    | {
        value: string
        label: string
        type: 'Date' | 'Select'
        opt?: { label: string; value: string }[]
        fn?: never
    }
    | {
        value: string
        label: string
        type: 'Autocomplete'
        fn: (q: string, cb: any) => void
        opt?: never
    }

const props = defineProps<{
    options: Option[]
}>()
const emit = defineEmits<{
    search: [form: Record<string, any>]
}>()

/* --------------- 数据 --------------- */
// 已选条件
const chosen = ref<Option[]>([])
// 剩余可选
const rest = computed(() =>
    props.options.filter(o => !chosen.value.map(c => c.value).includes(o.value))
)

// 真正收集查询值的对象
const searchForm = reactive<Record<string, any>>({})

/* --------------- 方法 --------------- */
const toAdd = ref('')

function add() {
    if (!toAdd.value) return
    const target = props.options.find(o => o.value === toAdd.value)!
    chosen.value.push(target)
    // 初始化对应字段
    if (target.type === 'Date') searchForm[target.value] = []
    else searchForm[target.value] = ''
    toAdd.value = ''
}

function remove(index: number) {
    const key = chosen.value[index].value
    chosen.value.splice(index, 1)
    delete searchForm[key]
}

function reset() {
    chosen.value = []
    Object.keys(searchForm).forEach(k => delete searchForm[k])
}

function emitQuery() {
    // 把空值/空数组过滤掉再抛给父级
    const clean: Record<string, any> = {}
    Object.keys(searchForm).forEach(k => {
        const v = searchForm[k]
        if (Array.isArray(v) ? v.length : v !== '') clean[k] = v
    })
    emit('search', clean)
}

// 禁止选未来日期
const disabledFuture = (time: Date) => time.getTime() > Date.now()
</script>

<style scoped>
.list-search {
    display: flex;
    flex-direction: column;
    gap: 8px;
    margin-bottom: 18px;
}

.item {
    display: flex;
    align-items: center;
    justify-content: space-between;
}

.add {
    display: flex;
    align-items: center;
    gap: 8px
}

.text {
    white-space: nowrap;
    width: 100px;
}

.item-btn {
    margin-left: 8px;
    width: 32px;
}
</style>