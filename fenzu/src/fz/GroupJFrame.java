package fz;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;

public class GroupJFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3394774975694421546L;

	Group g = new Group();
	
	// 创建按钮
	JButton jbt1 = new JButton("添加学生");
	JButton jbt2 = new JButton("分组");
	JButton jbt3 = new JButton("保存");
	JButton jbt4 = new JButton("抽取演讲人");
	JButton jbt5 = new JButton("显示分组情况");

//	//创建文本
	JTextField jf1 = null;
//	JTextField jf2 = null;

	// 下拉菜单
	JComboBox<String> co1 = new JComboBox<>();
	JComboBox<String> co2 = new JComboBox<>();

	// 消息对话框
	JDialog jd1 = new JDialog();
	JDialog jd2 = new JDialog();
	JDialog jd3 = new JDialog();

	// 标签
	Font font = new Font("宋体", Font.PLAIN, 20);
	JLabel jl1 = new JLabel("添加学生的人数:");
	JLabel jl2 = new JLabel("每组人数:");
	JLabel jl3 = new JLabel("学生总人数：");
	JLabel jl4 = new JLabel(g.getStudentNum()+"");
	JLabel jl5 = new JLabel();

	// 文本区
	JTextArea jt1 = new JTextArea();
	JTextArea jt2 = new JTextArea();

	// 菜单
	JMenu menu1 = new JMenu("选项");
	JMenuItem mi1 = new JMenuItem("清空");
	JMenuItem mi2 = new JMenuItem("关闭");

	JMenu menu2 = new JMenu("关于我们");
	JMenuItem mi10 = new JMenuItem("QQ");
	JMenuItem mi11 = new JMenuItem("微信");

	public GroupJFrame() {
		// 标题
		super("分组");
		// 界面
		this.setSize(850, 600);
		this.setResizable(false);
		// 置顶
		this.setAlwaysOnTop(true);
		// 居中
		this.setLocationRelativeTo(null);

		// 取消居中放置
		this.setLayout(null);

		// 添加文本域
		JPanel jp = new JPanel();
		this.add(jp);

		jl1.setBounds(20, 20, 150, 32);
		jl1.setFont(font);
		this.add(jl1);

		jl2.setBounds(320, 20, 100, 32);
		jl2.setFont(font);
		this.add(jl2);

		jl3.setBounds(550, 20, 120, 32);
		jl3.setFont(font);
		this.add(jl3);

		jl4.setBounds(670, 20, 40, 32);
		jl4.setFont(font);
		this.add(jl4);

		// 图片
		JLabel jl10 = new JLabel(new ImageIcon("image\\qq.jpg"));
		JLabel jl11 = new JLabel(new ImageIcon("image\\weixin.jpg"));

		jl10.setBounds(0, 0, 400, 600);
		// 将图片添加到对话框
		jd1.getContentPane().add(jl10);
		// 设置弹框大小
		jd1.setSize(600, 600);
		jd1.setAlwaysOnTop(true);
		jd1.setLocationRelativeTo(null);
		// 弹框不关闭则无法关闭
		jd1.setModal(true);

		jl11.setBounds(0, 0, 400, 600);
		// 将图片添加到对话框
		jd2.getContentPane().add(jl11);
		// 设置弹框大小
		jd2.setSize(600, 600);
		jd2.setAlwaysOnTop(true);
		jd2.setLocationRelativeTo(null);
		// 弹框不关闭则无法关闭
		jd2.setModal(true);

		jf1 = new JTextField();
		jf1.setBounds(420, 20, 80, 32);

//		jf2 = new JTextField();
//		jf2.setFont(font);
//		jf2.setBounds(20, 150, 500, 300);
//		jf2.setEditable(false);
//		this.add(jf2);

//		jl5.setBounds(20,150,500,400);
//		jl5.setFont(font);
//		this.add(jl5);

		// 文本区
//		jt1.setBounds(20,150,530,360);
		jt1.setFont(font);
//		this.add(jt1);

		// 滚动条
		JScrollPane scrollPane = new JScrollPane(jt1);
		// 总是显示滚动条
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(20, 150, 530, 360);
		this.add(scrollPane);

		// 下拉菜单设置
		co1.addItem("1");
		co1.addItem("2");
		co1.addItem("3");
		co1.addItem("4");
		co1.addItem("5");
		co1.addItem("6");
		co1.addItem("7");
		co1.addItem("8");
		co1.addItem("9");
		co1.addItem("10");
		co1.addItem("11");
		co1.addItem("12");
		co1.setBounds(170, 20, 80, 32);
		co1.setFont(font);
		this.add(co1);

		co2.addItem("1");
		co2.addItem("2");
		co2.addItem("3");
		co2.addItem("4");
		co2.addItem("5");
		co2.setEditable(false);

		co2.setBounds(420, 20, 80, 32);
		co2.setFont(font);
		this.add(co2);

		// 设置按钮坐标
		jbt1.setBounds(50, 100, 120, 40);
		jbt2.setBounds(230, 100, 120, 40);
		jbt3.setBounds(570, 100, 120, 40);
		jbt4.setBounds(400, 100, 120, 40);
		jbt5.setBounds(570, 180, 120, 40);

		jbt1.addActionListener(this);
		jbt2.addActionListener(this);
		jbt3.addActionListener(this);
		jbt4.addActionListener(this);
		jbt5.addActionListener(this);

		this.getContentPane().add(jbt1);
		this.getContentPane().add(jbt2);
		this.getContentPane().add(jbt3);
		this.getContentPane().add(jbt4);
		this.getContentPane().add(jbt5);

		// 初始化菜单
		JMenuBar menubar = new JMenuBar();
		menubar.setFont(font);
		this.setJMenuBar(menubar);

		menubar.add(menu1);
		menubar.add(menu2);

		ButtonGroup bg1 = new ButtonGroup();
		bg1.add(mi1);
		bg1.add(mi2);
		menu1.add(mi1);
		menu1.add(mi2);

		bg1.add(mi10);
		bg1.add(mi11);
		menu2.add(mi10);
		menu2.add(mi11);

		mi1.addActionListener(this);
		mi2.addActionListener(this);
		mi10.addActionListener(this);
		mi11.addActionListener(this);

		// 显示
		this.setVisible(true);
		// 设置关闭模式
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自动生成的方法存根
		Object source = e.getSource();

		if (source == jbt1) {
			String s = (String) co1.getSelectedItem();
			int num = Integer.parseInt(s);
			g.addStudent(num);
			g.setFenGroup(true);
			jl4.setText("" + g.getStudentNum());
			JOptionPane.showMessageDialog(this, "成功添加" + s + "名学生");

		} else if (source == jbt2) {
			if (g.getFenGroup()) {
				String s = (String) co2.getSelectedItem();
				int num = Integer.parseInt(s);
				g.selectGroup(num);
				g.setFenGroup(false);
				this.add(jf1);
				co2.removeAll();
				co2.addItem(s);
				co2.setEditable(false);
				JOptionPane.showMessageDialog(this, "成功分组");
			}

		} else if (source == jbt3) {
			IOFile.inGroupFile(g);
			JOptionPane.showMessageDialog(this, "保存成功");
		} else if (source == jbt4) {
			g.selectStudent();
			JOptionPane.showMessageDialog(this, "成功抽取" + g.getGroupNum() + "名演讲人");
		} else if (source == jbt5) {
			for (int i = 0; i < g.getGroupNum(); i++) {
				String text = "第"+(i+1)+"组";
				jt1.append(text);
				text = g.lookGroupName(i);// 名字
				jt1.append(text);
				text = g.lookGroupId(i);// id号
				jt1.append(text);

				// 获取演讲人
				text = g.showGroupSpeaker(i) + "\n";
				jt1.append(text);
			}
		} else if (source == mi1) {
			g.clearAll(0);
			g.setFenGroup(false);
			jl4.setText("" + g.getStudentNum());
			jt1.setText("");
		} else if (source == mi2) {
			System.exit(0);
		} else if (source == mi10) {
			// 显示弹框
			jd1.setVisible(true);
		} else if (source == mi11) {
			// 显示弹框
			jd2.setVisible(true);
		}

	}

}
