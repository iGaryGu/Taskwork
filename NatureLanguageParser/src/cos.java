import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class cos {
		private static final String driver = "com.mysql.jdbc.Driver";
		private static final String url = "jdbc:mysql://127.0.0.1/api_pattern";
		private static String username = "root";
		private static String password = "gary";
		private static final String selectKeySQL = "select REGULARIZED_ID from api_pattern_keyword";
		private static final String selectKeySQL1 = "select * from api_pattern_keyword where REGULARIZED_ID=(?)";
		private static ArrayList<Integer> array = new ArrayList<Integer>();
		private static double Threadhold = 0.05;
		static Map<String,Double> map1 = new HashMap<String,Double>();
		static Map<String,Double> cosmap = new HashMap<String,Double>();
		static int length = 0;
		static int  weight_lndex = 0;
		
		public static void getid() throws ClassNotFoundException, SQLException{
			Connection conn;
			Class.forName(driver);
			conn  = (Connection) DriverManager.getConnection(url, username, password);
			PreparedStatement selectStatement = (PreparedStatement) conn.prepareStatement(selectKeySQL);
			ResultSet rs = selectStatement.executeQuery();
			int id;
			int index = 0;
			while(rs.next()){
				id = rs.getInt("REGULARIZED_ID");
				if(!array.contains(id))
					array.add(index++, id);
			}
//			for(int i =0 ;i < array.size();i++){
//				System.out.println(array.get(i));
//			}
		}
		public static void cos(Map<String,Double> inputmap) throws ClassNotFoundException, SQLException, IOException{
			//connect to mysql
			Connection conn;
			Class.forName(driver);
			conn  = (Connection) DriverManager.getConnection(url, username, password);
			
			double cosine = 0;
			double temp = 0;
			double length1 = 0;
			double length2 = 0;
			System.out.println("arraysize  "+array.size());
			
			for(int i = 0; i < array.size();i++){	
					//initial value
					length1  = 0;
					length2 = 0;
					temp = 0;
					PreparedStatement selectStatement1 = (PreparedStatement) conn.prepareStatement(selectKeySQL1);
					selectStatement1.setInt(1,array.get(i));
					//get keywords and tf-idf
					ResultSet rs1 = selectStatement1.executeQuery();
					while(rs1.next()){
						map1.put(rs1.getString(2), rs1.getDouble(3));
					}
					Set<String> set1 = map1.keySet();
						
					//分子
					for(int k = 0 ; k < set1.size();k++){
						//calculate the value between two ID by test the keywords
						if(inputmap.containsKey(set1.toArray()[k].toString())){
//							System.out.println(set1.toArray()[k]);
//							System.out.println(map1.get(set1.toArray()[k])+"*"+inputmap.get(set1.toArray()[k]));
							temp = temp + map1.get(set1.toArray()[k])*inputmap.get(set1.toArray()[k]);
						}
					}
						
					//both ID tf-idf length
					//分母
					for(int k = 0 ; k < map1.size();k++){
						length1 = length1 + Math.pow((double) map1.values().toArray()[k], 2);
					}
					for(int k = 0 ; k < inputmap.size();k++){
						length2 = length2 + Math.pow((double) inputmap.values().toArray()[k], 2);
					}
					//開根號
					length1 = Math.sqrt(length1);
					length2 = Math.sqrt(length2);
//					System.out.println("temp "+temp);
//					System.out.println("length1 "+length1);
//					System.out.println("length2 "+length2);
					cosine = temp/(length1*length2);
					cosmap.put(String.valueOf(array.get(i)).toString(), cosine);
//					System.out.println("cosine = "+cosine);
				map1.clear();
			}
			List<Map.Entry<String,Double>> list_data = new ArrayList<Map.Entry<String,Double>>(cosmap.entrySet());
			Collections.sort(list_data, new Comparator<Map.Entry<String,Double>>(){
				public int compare(Map.Entry<String,Double> entry1,Map.Entry<String,Double> entry2){
					if ((entry2.getValue() - entry1.getValue())>0){
						return 1;
					}
					else if((entry2.getValue() - entry1.getValue())==0){
						return 0;
					}
					else{
						return -1;
					}
				}
			});
			for(int i = 0 ; i < list_data.size();i++){
				System.out.println(list_data.get(i));
			}
//			System.out.println(list_data);
		}
		
		
//		public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException{
////			getid();
////			cos();
//		}
	
	
}
