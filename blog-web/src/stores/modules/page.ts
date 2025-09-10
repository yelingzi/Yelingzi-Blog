// stores/pageStore.js
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const usePageStore = defineStore('page',
  () => {

    const components = ref<any>([])
    const layout = ref({
      width: 1200,
      height: 800
    })


    const addComponent = (component: any) => {
      components.value.push(component);
      console.log(component.value)
    }
    const updateComponent = (index: string | number, newComponent: any) => {
      components.value[index] = newComponent;
    }
    const removeComponent = (index: any) => {
      components.value.splice(index, 1);
    }

    return {
      addComponent,
      updateComponent,
      removeComponent,
      components
    }
  },
  {
    persist: true,
  },
);
