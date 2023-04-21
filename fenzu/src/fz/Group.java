package fz;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Group {
	
	private static BufferedReader br;
	
	Group() 
	{
		try {
			br = new BufferedReader(new FileReader("group.txt"));
			int lineNum=0;//记录行数
			String line = br.readLine();
			while(line != null )
			{
				System.out.println(line.length()+"\n");
				String []st = line.split("，");
				if(st.length !=2)
				{
					System.out.println(lineNum +"行信息有误");
					return;
				}
				Student stu = new Student(st[0],Integer.parseInt(st[1]));
				student.add(stu);
				setFenGroup(true);
				line = br.readLine();
				lineNum++;
			}


		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
	}

	// 记录学生
	private ArrayList<Student> student = new ArrayList<>();

	// 记录小组
	private ArrayList<ArrayList<Student>> group = new ArrayList<>();

	// 记录演讲人
	private ArrayList<Student> speaker = new ArrayList<>();

	// 分组开关
	private boolean fenGroup = false;

	// 获取分组开关
	public boolean getFenGroup() {
		return fenGroup;
	}

	// 设置分组开关
	public void setFenGroup(boolean b) {
		this.fenGroup = b;
	}

	// 获取学生人数
	public int getStudentNum() {
		if (student.isEmpty()) {
			return 0;
		}
		return student.size();
	}
	
	//获取学生数组中的数据
	public String getStudentData(int i)
	{
		if (student.isEmpty() || i > student.size()) {
			return "";
		}
		return student.get(i).getName() + "，" + student.get(i).getId()+"\n";
	}

	// 获取小组人数
	public int getGroupNum() {
		if (group.isEmpty()) {
			return 0;
		}
		return group.size();
	}

	// 获取i组j位的学生名字
	public String getGroupStudentName(int i, int j) {
		if (group.isEmpty()) {
			return "";
		}
		return group.get(i).get(j).getName();
	}

	// 获取i组j位的学生id
	public int getGroupStudentId(int i, int j) {
		if (group.isEmpty()) {
			return 0;
		}
		return group.get(i).get(j).getId();
	}

	// 获取每组成员人数
	public int getGroupStudentNum(int num) {
		if (group.isEmpty()) {
			return 0;
		}
		return group.get(num).size();
	}

	// 随机添加学生
	public void addStudent(int num) {
		int count = 0;// 计算学生id
		int addStudent = 0;// 计算添加的学生
		// 从1001开始计算
		if (student != null) {
			count = 1001 + student.size();
			addStudent = student.size();
		} else {
			count = 1001;
		}

		String x = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String name = "张三";
		for (int i = addStudent; i < (addStudent + num); i++) {
			Student st = new Student(name + x.charAt(i), count);
			student.add(st);
			count++;
		}
	}

	// 分组，num个为一组
	public void selectGroup(int num) {
		if (student.isEmpty() || num < 1) {
			System.out.println("学生或小组数量为0，无法分组");
			return;
		}

		ArrayList<Student> stu = new ArrayList<>();
		Random r = new Random();
		int addStudent = 0;// 需要存放的学生
		int t = 0;// 随机数

		if (!group.isEmpty()) {
			int temp = (group.size() - 1) * num + group.get(group.size() - 1).size();
			addStudent = student.size() - temp;
			this.copyFenGroupStudent(stu, temp, student.size());// 深拷贝存放的学生
			int addStu = num;
			if (stu.size() > num - group.get(group.size() - 1).size()) {
				addStu = num - group.get(group.size() - 1).size();
			}
			ArrayList<Student> st = new ArrayList<Student>(group.get(group.size() - 1));
			group.remove(group.get(group.size() - 1));
			for (int i = 0; i < addStu; i++) {
				t = r.nextInt(addStudent);
				Student s = new Student(stu.get(t).getName(), stu.get(t).getId());
				st.add(s);
				addStudent--;
				this.exchangeStudent(stu, t, addStudent);

			}
			group.add(st);

		} else {
			addStudent = student.size();
			this.copyFenGroupStudent(stu, 0, addStudent);// 深拷贝存放的学生
		}

		int Group1 = addStudent / num;// 满成员小组个数
		int Group2 = addStudent % num;// 小于num个数的学生为新的一组

		for (int i = 0; i < Group1; i++) {
			ArrayList<Student> st = new ArrayList<>();
			for (int j = 0; j < num; j++) {
				t = r.nextInt(addStudent);
				Student s = new Student(stu.get(t).getName(), stu.get(t).getId());
				st.add(s);
				addStudent--;
				this.exchangeStudent(stu, t, addStudent);
			}
			group.add(st);

		}
		if (Group2 > 0) {
			ArrayList<Student> st = new ArrayList<>();
			for (int i = 0; i < Group2; i++) {
				Student s = new Student(stu.get(i).getName(), stu.get(i).getId());
				st.add(s);
			}
			group.add(st);
		}

	}

	// 小组成员交换
	private void exchangeStudent(ArrayList<Student> stu, int qian, int hou) {
		Student temp = new Student(stu.get(hou).getName(), stu.get(hou).getId());
		stu.get(hou).addStudentDate(stu.get(qian).getName(), stu.get(qian).getId());
		stu.get(qian).addStudentDate(temp.getName(), temp.getId());
	}

	// 深拷贝学生数据
	private void copyFenGroupStudent(ArrayList<Student> stu, int existStudent, int allStudent) {
		for (int i = 0; i < allStudent - existStudent; i++) {
			Student s = new Student(student.get(existStudent + i).getName(), student.get(existStudent + i).getId());
			stu.add(s);
		}
	}

	// 从小组中抽取演讲人
	public void selectStudent() {
		if (group.isEmpty()) {
			System.out.println("小组为空，不能抽取演讲人");
			return;
		}

		int t = 0;// 记录抽取到的随机数
		Random r = new Random();// 随机数函数
		int speakerNum = 0;// 演讲人数
		if (!speaker.isEmpty()) {
			speakerNum = speaker.size();
			// 如果最后一组没满员重新抽取
			if (group.get(0).size() != group.get(speakerNum - 1).size()) {
				speakerNum--;
				speaker.remove(speakerNum - 1);
			}
		}

		for (int i = speakerNum; i < this.group.size(); i++) {
			int size = this.group.get(i).size();
			t = r.nextInt(size);
			Student stu = new Student(group.get(i).get(t).getName(), group.get(i).get(t).getId());
			speaker.add(stu);
		}

	}

	// 显示演讲人
	public String showGroupSpeaker(int num) {
		if (speaker.isEmpty()) {
			return "演讲人为空\n";
		}
		if (num > -1 && num < speaker.size()) {
			return ("第" + (num + 1) + "组" + "演讲人为：" + speaker.get(num).getName() + " id:" + speaker.get(num).getId()
					+ "\n");
		} else if (num >= speaker.size()) {
			return ("第" + (num + 2) + "组未抽取演讲人\n");
		} else {
			return ("查看演讲人失败，输入数据错误！\n");
		}

	}

	// 查看小组成员名字
	public String lookGroupName(int num) {
		if (group.isEmpty()) {
			return ("小组为空，不能查看");
		}

		String getName = ("小组成员名字为：");
		for (int i = 0; i < this.group.get(num).size(); i++) {
			getName += ("\t" + this.group.get(num).get(i).getName());
		}
		getName += "\n";
		return getName;
	}

	// 查看小组成员id
	public String lookGroupId(int num) {
		if (group.isEmpty()) {
			return ("小组为空，不能查看");
		}

		String getId = "小组成员id为：";
		for (int i = 0; i < this.group.get(num).size(); i++) {
			getId += ("\t" + this.group.get(num).get(i).getId());
		}
		getId += "\n";
		return getId;
	}

	// 全部清空
	public void clearAll(int num) {
		if (num == 0) {
			student.clear();
			group.clear();
			speaker.clear();
			System.out.println("清空成功");
		}
	}
	// 结束类
}


class Student {
	
	private String studentName;// 学生名字

	private int studentId;// 学生id

	public Student() {		
	}// 无参构造函数

	// 有参构造函数
	public Student(String name, int id) {
		this.studentName = name;
		this.studentId = id;
	}

	// 添加学生信息
	public void addStudentDate(String name, int id) {
		this.studentName = name;
		this.studentId = id;
	}

	public String getName() {
		return this.studentName;
	}

	public int getId() {
		return this.studentId;
	}
}
