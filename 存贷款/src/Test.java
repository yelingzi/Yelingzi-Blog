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
import javax.swing.JTextField;

public class Test extends JFrame implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7681583431045719268L;

	//设置字体
	Font font = new Font("宋体", Font.PLAIN, 20);
	
	//判断是贷款还是存款
	boolean cunkuan = true;

	// 创建按钮
	JButton jbt1 = new JButton("存款");
	JButton jbt2 = new JButton("贷款");
	JButton jbt3 = new JButton("计算");
	
	
	// 菜单
	JMenu menu1 = new JMenu("选项");
	JMenuItem mi1 = new JMenuItem("恢复默认");
	JMenuItem mi2 = new JMenuItem("关闭");

	JMenu menu2 = new JMenu("关于我们");
	JMenuItem mi10 = new JMenuItem("QQ");
	JMenuItem mi11 = new JMenuItem("微信");
	
	// 标签
	JLabel jl1 = new JLabel("存款利率：3.25%");
	JLabel jl2 = new JLabel("贷款利率：4.75%");
	JLabel jl3 = new JLabel("本金：");
	JLabel jl4 = new JLabel("存款年限：");
	JLabel jl5 = new JLabel("年末追加金额：");
	JLabel jl6 = new JLabel("x年后可得到本息和为：");
	JLabel jl7 = new JLabel("0.00");
	JLabel jl8 = new JLabel("");
	JLabel jl9 = new JLabel("");
	JLabel jl0 = new JLabel("信计3班第二小组");
	
	// 消息对话框
	JDialog jd1 = new JDialog();
	JDialog jd2 = new JDialog();
	
	//文件域 
	JTextField jtf1 =new JTextField();
	JTextField jtf2 =new JTextField();
	JTextField jtf3 =new JTextField();
	
	// 下拉菜单
	JComboBox<String> co1 = new JComboBox<>();
	
	public Test() {
		// 标题
		super("理财计算器");
		// 界面
		this.setSize(600, 500);
		this.setResizable(false);
		// 置顶
		this.setAlwaysOnTop(true);
		// 居中
		this.setLocationRelativeTo(null);

		// 取消居中放置
		this.setLayout(null);
		
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
		
		//设置文本域信息
		jtf1.setBounds(50, 150, 120, 40);
		jtf1.setFont(font);

		jtf2.setBounds(50, 240, 120, 40);
		jtf2.setFont(font);		
		
		jtf3.setBounds(50, 330, 120, 40);
		jtf3.setFont(font);
		this.add(jtf1);
		this.add(jtf2);
		this.add(jtf3);
		
		//设置标签信息
		jl1.setBounds(400, 50, 160, 40);
		jl1.setFont(font);
		this.add(jl1);
		
		jl2.setBounds(400, 100, 160, 40);
		jl2.setFont(font);
		this.add(jl2);
		
		jl3.setBounds(50, 110, 160, 40);
		jl3.setFont(font);
		this.add(jl3);
		
		jl4.setBounds(50, 200, 160, 40);
		jl4.setFont(font);
		this.add(jl4);
		
		jl5.setBounds(50, 290, 160, 40);
		jl5.setFont(font);
		this.add(jl5);
		
		//输出结果
		jl6.setBounds(230, 200, 300, 40);
		jl6.setFont(font);
		this.add(jl6);
		
		jl7.setBounds(230, 240, 300, 40);
		jl7.setFont(font);
		this.add(jl7);
		
		jl8.setBounds(230, 280, 300, 40);
		jl8.setFont(font);
		this.add(jl8);
		
		jl9.setBounds(230, 320, 300, 40);
		jl9.setFont(font);
		this.add(jl9);
		
		jl0.setBounds(0, 0, 180, 40);
		jl0.setFont(font);
		this.add(jl0);
		
		//下拉菜单
		co1.addItem("等额本息");
		co1.addItem("等额本金");
		co1.setBounds(50, 330, 120, 40);
		co1.setFont(font);
		
		// 设置按钮坐标
		jbt1.setBounds(50, 50, 120, 40);
		jbt2.setBounds(230, 50, 120, 40);
		jbt3.setBounds(230, 150, 120, 40);
		
		//添加监控
		jbt1.addActionListener(this);
		jbt2.addActionListener(this);
		jbt3.addActionListener(this);
		
		this.getContentPane().add(jbt1);
		this.getContentPane().add(jbt2);
		this.getContentPane().add(jbt3);
		
		
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
	
	public void actionPerformed(ActionEvent e) {
		// TODO 自动生成的方法存根
		Object source = e.getSource();

		if (source == jbt1) 
		{
			this.remove(co1);
			this.add(jtf3);
			jl3.setText("本金：");
			jl4.setText("存款年限：");
			jl5.setText("年末追加金额：");
			jl6.setText("x年后可得到本息和为：");
			jl7.setText("0.00");
			jl8.setText("");
			jl9.setText("");
			cunkuan = true;
			this.repaint();
		}else if(source == jbt2)
		{
			this.remove(jtf3);
			jl3.setText("贷款金额：");
			jl4.setText("贷款年限：");
			jl5.setText("还款方式：");
			jl6.setText("x年后共需要还款：");
			jl7.setText("0.00");
			this.add(co1);
			cunkuan = false;
			this.repaint();
		}else if(source == jbt3)
		{
			if(YoNjisuan(jtf1.getText())=="?") 
			{
				JOptionPane.showMessageDialog(this, "存款或者贷款金额为空");
				return;
			}
			if(YoNjisuan(jtf2.getText())=="?")
			{
				JOptionPane.showMessageDialog(this, "存款或者贷款年限为空");
				return;
			}
			if(YoNjisuan(jtf1.getText())=="/") 
			{
				JOptionPane.showMessageDialog(this, "存款或者贷款金额含有非法字符");
				return;
			}
			if(YoNjisuan(jtf2.getText())=="/")
			{
				JOptionPane.showMessageDialog(this, "存款或者贷款年限含有非法字符");
				return;
			}
			//存款
			if(cunkuan == true)
			{
				double yearMoney = 0;
				if(YoNjisuan(jtf3.getText())!="?" && YoNjisuan(jtf3.getText())!="/")
				{
					yearMoney = Double.parseDouble(jtf3.getText());
				}
				
				double money = Double.parseDouble(jtf1.getText());
				double year = Integer.parseInt(jtf2.getText());
				double count = cunkuanjisuan(money,year,yearMoney);
				//保留两位小数
				String st = String.format("%.2f", count);
				jl6.setText(year+"年后可得到本息和为：");
				jl7.setText(st);	
				this.repaint();
				
			}			
			else//贷款
			{
				//判断还款方式
				//等额本息
				if(co1.getSelectedIndex()==0)
				{
					double money = Double.parseDouble(jtf1.getText());
					double moon = Integer.parseInt(jtf2.getText()) * 12;
					double moonMoney = benxi(money,moon);
					double count = moonMoney * moon;
					String st1 = String.format("%.2f", moonMoney);
					String st2 = String.format("%.2f", count);
					jl6.setText((moon/12)+"年后共需要还款：");
					jl7.setText(st2);
					jl8.setText("第一个月还款金额：" + st1);
					jl9.setText("最后一个月还款金额：" + st1);
					this.repaint();
				}
				//等额本金
				else if(co1.getSelectedIndex()==1)
				{
					double money = Double.parseDouble(jtf1.getText());
					double moon = Integer.parseInt(jtf2.getText()) * 12;
					double lilv = 0.0475/12;
					//公式
					//第k个月还款金额 = 贷款金额/月份数 + 贷款金额*（1-（k-1）/月份数）*利率
					double oneMoonMoney = money/moon + money*(1-(1-1)/moon)*lilv;
					double endMoonMoney = money/moon + money*(1-(moon-1)/moon)*lilv;
					//总还款金额 = 贷款金额 + 贷款金额 * 利率 *（月份数+1）/2
					double count = money + money * lilv*(moon+1)/2;
					String st1 = String.format("%.2f", oneMoonMoney);
					String st2 = String.format("%.2f", endMoonMoney);
					String st3 = String.format("%.2f", count);
					jl6.setText((moon/12)+"年后共需要还款：");
					jl7.setText(st3);
					jl8.setText("第一个月还款金额：" + st1);
					jl9.setText("最后一个月还款金额：" + st2);
					this.repaint();
					
				}
				
			}
			
		}
		else if (source == mi1) {
			//清空
			this.remove(co1);
			this.add(jtf3);
			jl3.setText("本金：");
			jl4.setText("存款年限：");
			jl5.setText("年末追加金额：");
			jl6.setText("x年后可得到本息和为：");
			jl7.setText("0.00");
			jl8.setText("");
			jl9.setText("");
			jtf1.setText("");
			jtf2.setText("");
			cunkuan = true;
			this.repaint();
			
		} else if (source == mi2) {
			//关闭
			System.exit(0);
		} else if (source == mi10) {
			// 显示弹框
			jd1.setVisible(true);
		} else if (source == mi11) {
			// 显示弹框
			jd2.setVisible(true);
		}
		
		
	}
	
	//判断数据是否可以计算
	public String YoNjisuan(String str)
	{
		char[]ch=str.toCharArray();
		if(ch.length ==0)
		{
			return "?";
		}
		for(int i = 0;i < ch.length;i++)
		{
			if(ch[i] > 57 || ch[i] < 48)
			{
				return "/";
			}
		}
		return str;
	}
		
	//计算利息
	public double cunkuanjisuan(double money,double year,double yearMoney )
	{
		double count = money;
		for(int i=0;i<year;i++)
		{
			count = count *(1+0.0325);
			count +=yearMoney;
		}
		return count;
	}
	
	//等额本息 每个月还款金额
	public double benxi(double money,double moon)
	{
		//公式
		//每个月还款金额 = 贷款金额 * 每月利息 * (1+每月利息)^还款月份 / (1+每月利息)^还款月份-1
		double r = 0.0475/12;
		double x =(1 + r);
		for(int i = 1; i < moon; i++)
		{
			x = x*(1 + r);
		}
		return (money * r * x) / ( x-1 );
	}
	
}
