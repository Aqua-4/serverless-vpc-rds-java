package com.serverless;

import java.util.Collections;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.sql.*;
//import com.mysql.cj.jdbc.Driver;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger LOG = LogManager.getLogger(Handler.class);
	String resp = "";

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		LOG.info("received: {}", input);


		try{
			Class.forName("com.mysql.cj.jdbc.Driver");

			String con_url =   "jdbc:mysql://"
					+ System.getenv("RDS_HOSTNAME")
					+ "/"
					+ System.getenv("RDS_DB_NAME");

			System.out.println(con_url);

			Connection con=DriverManager.getConnection(
					con_url,System.getenv("RDS_USERNAME"),System.getenv("RDS_PASSWORD"));
			Statement stmt=con.createStatement();
			ResultSet rs=stmt.executeQuery("select * from employees");
			while(rs.next())
				resp = resp + "\n"+ rs.getInt(1)+"  "+rs.getString(2);
			con.close();

			Response responseBody = new Response(resp, input);

			return ApiGatewayResponse.builder()
					.setStatusCode(200)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & serverless"))
					.build();

		}catch(Exception e){
			LOG.info("exception: {}", e);

			Response responseBody = new Response("Error: "+e.toString(), input);
			return ApiGatewayResponse.builder()
					.setStatusCode(500)
					.setObjectBody(responseBody)
					.setHeaders(Collections.singletonMap("X-Powered-By", "AWS Lambda & Serverless"))
					.build();
		}



	}
}
