package fz;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class IOFile {
	private static BufferedWriter bw;

	//写文件
	public static void inGroupFile(Group g)
	{
		try {
			bw = new BufferedWriter(new FileWriter("group.txt"));
			for(int i = 0;i < g.getStudentNum();i++)
			{
				String st = ("");
				st = g.getStudentData(i);
				bw.write(st);
			
			}
			bw.close();
				
				
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			System.out.println("保存失败");
		}
	}
	
	
}
