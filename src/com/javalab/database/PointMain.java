package com.javalab.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PointMain {

	// [멤버 변수]
	/** database 관련 문자열 상수 선언 **/
	// 1. oracle 드라이버 이름 문자열 상수
	public static final String DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";
	// 2. oracle 데이터베이스 접속 경로 문자열 상수
	public static final String DB_URL = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";

	/** database 관련 객체 선언 **/
	// 1. 데이터베이스 접속 객체
	public static Connection con = null;
	// 2. query 실행 객체
	public static Statement stmt = null;
	public static PreparedStatement pstmt = null;
	// 3. selcet 결과 저장 객체
	public static ResultSet rs = null;

	/** 그 외 필요한 객체, 변수 선언 **/
	// 4. oracle 계정(id/pwd)
	public static String oracleId = "tempdb";
	// 5. oracle Password
	public static String oraclePwd = "1234";

	/*
	 * 메인 메소드 [주의]한단계를 진행하고 커넥션 객체를 닫기 때문에 실행한 기능은 주석처리를 하고 다음 단계로 진행할것.
	 */
	public static void main(String[] args) {

		//// 각 메소드 호출
		// 1. 디비 접속 메소드 호출
		connectDB();

		// 2. 회원들과 보유 포인터 정보 조회
		getMeberAndPoint();
		System.out.println();

		// 3. 이소미 회원에게 포인트 15점 추가 지급
		updatePointSomi();
		System.out.println();

		// 4. 관리자에게 포인트 30점 추가 지급
		upadtePointmanager();
		System.out.println();

		// 5. 전체 회원 평균 포인트보다 작은 회원 목록 조회
		getMembersLessThanAvg();

		// 6. 자원 반납
		closeResource();

	} // end main()

	private static void getMembersLessThanAvg() {
		try {
			String sql = "select  m.USER_ID, m.NAME, m.PWD, m.EMAIL, m.PHONE, decode(m.ADMIN, 0, '일반사용자',1,'관리자') admin, p.POINTS, to_char(p.reg_date,'yyyy-mm-dd') reg_date";
			sql += " from member m left OUTER join point p on m.user_id = p.user_id";
			sql += " where p.points < (select avg(p.points)";
			sql += " from point p)";

			pstmt = con.prepareStatement(sql);

			rs = pstmt.executeQuery();

			System.out.println("USER_ID " + "\t" + " NAME " + "\t" + " PWD " + "\t" + " EMAIL " + "\t" + " PHONE "
					+ "\t" + " admin " + "\t" + " POINTS " + "\t" + " reg_date ");

			while (rs.next()) {
				System.out.println(rs.getString("USER_ID") + "\t" + rs.getString("NAME") + "\t" + rs.getString("PWD")
						+ "\t" + rs.getString("EMAIL") + "\t" + rs.getString("PHONE") + "\t" + rs.getString("admin")
						+ "\t" + rs.getInt("POINTS") + "\t" + rs.getString("reg_date") + "\t");
			}
			System.out.println("===========================================================");
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			closeResource(pstmt, rs);
		}
		System.out.println();
	}

	private static void upadtePointmanager() {

		String sql = " ";
		try {
			int intPoint = 30;
			int admin = 0;

			// PreparedStatement 객체에서 사용할 SQL문 생성
			sql = "update point";
			sql += " set points = points + ?";
			sql += " where user_id = ( select user_id from member m ";
			sql += " where admin = ?)";

			pstmt = con.prepareStatement(sql);

			// 쿼리문에 인자 전달
			pstmt.setInt(1, intPoint);
			pstmt.setInt(2, admin);

			// 쿼리 실행
			// 처리된 결과 반환됨 (수정된 행수)
			int resultRows = pstmt.executeUpdate();

			if (resultRows > 0) {
				System.out.println("수정 성공");
			} else {
				System.out.println("수정 실패");
			}
		} catch (SQLException e) {
			System.out
					.println("=======================================================================================");
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			// 자원 해제 메소드 호출
			closeResource(pstmt, rs);
		}
	}

	private static void updatePointSomi() {
		String sql = " ";
		try {
			// 수정할 회원 및 포인트 변수 선언
			int intPoint = 15;
			String strName = "이소미";

			// PreparedStatement 객체에서 사용할 SQL문 생성
			sql = "update point";
			sql += " set points = points + ?";
			sql += " where user_id = ( select user_id from member m ";
			sql += " where name = ?)";

			pstmt = con.prepareStatement(sql);

			// 쿼리문에 인자 전달
			pstmt.setInt(1, intPoint);
			pstmt.setString(2, strName);

			// 쿼리 실행
			// 처리된 결과 반환됨 (수정된 행수)
			int resultRows = pstmt.executeUpdate();

			if (resultRows > 0) {
				System.out.println("수정 성공");
			} else {
				System.out.println("수정 실패");
			}
		} catch (SQLException e) {
			System.out
					.println("=======================================================================================");
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			// 자원 해제 메소드 호출
			closeResource(pstmt, rs);
		}
	}

	private static void getMeberAndPoint() {

		try {
			// SQL 쿼리문 만들기
			String sql = "";
			sql += "select m.user_id, m.name, m.pwd, m.email, m.phone,";
			sql += " decode(m.admin, 0, '일반사용자', 1, '관리자') ADMIN,";
			sql += " p.point_id, p.points, to_char(p.reg_date, 'yyyy-mm-dd') reg_date";
			sql += " from member m left outer join point p on m.user_id = p.user_id";

			pstmt = con.prepareStatement(sql);
			// pstmt 객체의 executeQuery() 메소드를 통해서 쿼리 실행
			// 데이터 베이스에서 조회된 결과가 ResultSet 객체에 담겨옴
			rs = pstmt.executeQuery();

			System.out.println("회원정보 와 회원들의 포인터 정보 조회");
			System.out
					.println("=======================================================================================");
			System.out.println("user_id" + " " + "name" + " " + "pwd" + "\t" + "email" + "\t" + "phone" + "\t" + "admin"
					+ "\t" + "point_id" + "\t" + "points" + "\t" + "reg_date");
			System.out.println(
					"----------------------------------------------------------------------------------------");
			while (rs.next()) {
				System.out.println(rs.getString("user_id") + "\t" + rs.getString("name") + "\t" + rs.getInt("pwd")
						+ "\t" + rs.getString("email") + "\t" + rs.getString("phone") + "\t" + rs.getString("admin")
						+ "\t" + rs.getInt("point_id") + "\t" + rs.getInt("points") + "\t" + rs.getString("reg_date")
						+ "\t");
			}

		} catch (SQLException e) {
			System.out
					.println("=======================================================================================");
			System.out.println("SQL ERR! : " + e.getMessage());
		} finally {
			// 자원 해제 메소드 호출
			closeResource(pstmt, rs);
		}
	}

	// 드라이버 로딩과 커넥션 객체 생성 메소드
	private static void connectDB() {
		try {
			Class.forName(DRIVER_NAME);
			System.out.println("1. 드라이버 로드 성공!");

			con = DriverManager.getConnection(DB_URL, oracleId, oraclePwd);
			System.out.println("2. 커넥션 객체 생성 성공!");

		} catch (ClassNotFoundException e) {
			System.out.println("드라이버 ERR! : " + e.getMessage());
		} catch (SQLException e) {
			System.out.println("SQL ERR! : " + e.getMessage());
		}
	} // end connectDB()

	// 커넥션 객체 자원 반환
	private static void closeResource() {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			System.out.println("6. 자원해제 ERR! : " + e.getMessage());
		}
	}

	// 커넥션 객체 자원 반환
	private static void closeResource(PreparedStatement pstmt, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
		} catch (SQLException e) {
			System.out.println("자원해제 ERR! : " + e.getMessage());
		}
	}
}