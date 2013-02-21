package com.iecho.webservice;

import java.util.HashMap;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

public class WebServiceConnection {
	private SoapObject request;
	private AndroidHttpTransport httpTransport;
	private SoapSerializationEnvelope envelope;
	public JSONObject responseList;
	public HashMap<String, String> hashMap;
	public HashMap<String, Object> userProfileHashMap;
	public WebServiceConnection() {

	}
	/**
	 * Web service for login,decline friend request,follow you request
	 * @param calling_service_name
	 * @param soap_action
	 * @param target_namespace
	 * @param operation_name
	 * @param service_url
	 * @param key_1
	 * @param key_2
	 * @param val_1
	 * @param val_2
	 */
	public WebServiceConnection(String calling_service_name,String soap_action,String target_namespace,String operation_name,String service_url,String key_1,String key_2,String val_1,String val_2){
		request = new SoapObject(target_namespace, operation_name);
		request.addProperty(key_1, val_1);
		request.addProperty(key_2, val_2);
		System.out.println(request + "*****Request******");
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		System.out.println(envelope + "****Envelop******");
		envelope.setOutputSoapObject(request);
		httpTransport = new AndroidHttpTransport(service_url);
		try {
			System.out.println("before call");
			httpTransport.call(soap_action, envelope);
			System.out.println("after call");
			System.out.println(envelope.getResponse());
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			System.out.println(response + "********responce******* "+calling_service_name);
			getServiceResponse(response.toString(), calling_service_name);
		} catch (Exception exception) {
			System.out.println(exception + " WS******Exception*******");
		}
	}

	/**
	 * web service for registration
	 * @param calling_service_name
	 * @param soap_action
	 * @param target_namespace
	 * @param operation_name
	 * @param service_url
	 * @param f_name
	 * @param l_name
	 * @param phone_no
	 * @param email
	 * @param password
	 * @param image_
	 */
	public WebServiceConnection(String calling_service_name,String soap_action,String target_namespace,String operation_name,String service_url,String f_name,String l_name,String phone_no,String email,String password,byte[] image_){
		request = new SoapObject(target_namespace, operation_name);
		request.addProperty("firstname", f_name);
		request.addProperty("lastName", l_name);
		request.addProperty("phone", phone_no);
		request.addProperty("email", email);
		request.addProperty("password", password);
		request.addProperty("userProfilePic", image_);
		System.out.println(request + "*****Request******");
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		MarshalBase64 marshal=new MarshalBase64();
		marshal.register(envelope);
		System.out.println(envelope + "****Envelop******");
		envelope.setOutputSoapObject(request);
		httpTransport = new AndroidHttpTransport(service_url);
		try {
			System.out.println("before call");
			httpTransport.call(soap_action, envelope);
			System.out.println("after call");
			System.out.println(envelope.getResponse());
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			System.out.println(response + "********responce*******");
			getServiceResponse(response.toString(), calling_service_name);
		} catch (Exception exception) {
			System.out.println(exception + " WS******Exception*******");
		}
	}

	/**
	 * web service for forgot password,contact sync,view pending friend requests,Get Pending FollowYou Incoming Request,Get User Profile,User Current state
	 * @param calling_service_name
	 * @param soap_action
	 * @param target_namespace
	 * @param operation_name
	 * @param service_url
	 * @param key_1
	 * @param val_1
	 */
	public WebServiceConnection(String calling_service_name,String soap_action,String target_namespace,String operation_name,String service_url,String key_1,String val_1){
		request = new SoapObject(target_namespace, operation_name);
		request.addProperty(key_1, val_1);
		System.out.println(request + "*****Request******");
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		System.out.println(envelope + "****Envelop******");
		envelope.setOutputSoapObject(request);
		httpTransport = new AndroidHttpTransport(service_url);
		//		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		try {
			System.out.println("before call");
			httpTransport.call(soap_action, envelope);
			System.out.println("after call");
			System.out.println(envelope.getResponse());
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			System.out.println(response + "********responce*******");
			getServiceResponse(response.toString(), calling_service_name);
		} catch (Exception exception) {
			System.out.println(exception + " WS******Exception*******");
		}
	}

	/**
	 * web service for send new friend request
	 * @param calling_service_name
	 * @param soap_action
	 * @param target_namespace
	 * @param operation_name
	 * @param service_url
	 * @param key_1
	 * @param key_2
	 * @param val_1
	 * @param val_2
	 */
	public WebServiceConnection(String calling_service_name,String soap_action,String target_namespace,String operation_name,String service_url,String key_1,String key_2,String key_3,String val_1,String val_2,String val_3){
		request = new SoapObject(target_namespace, operation_name);
		request.addProperty(key_1, val_1);
		request.addProperty(key_2, val_2);
		request.addProperty(key_3, val_3);
		System.out.println(request + "*****Request******");
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		System.out.println(envelope + "****Envelop******");
		envelope.setOutputSoapObject(request);
		httpTransport = new AndroidHttpTransport(service_url);
		try {
			System.out.println("before call");
			httpTransport.call(soap_action, envelope);
			System.out.println("after call");
			System.out.println(envelope.getResponse());
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			System.out.println(response + "********responce*******");
			getServiceResponse(response.toString(), calling_service_name);
		} catch (Exception exception) {
			System.out.println(exception + " WS******Exception*******");
		}
	}
	
	
	public WebServiceConnection(String calling_service_name,String soap_action,String target_namespace,String operation_name,String service_url,String key_1,String key_2,String key_3,String key_4,String val_1,String val_2,String val_3, String val_4){
		request = new SoapObject(target_namespace, operation_name);
		request.addProperty(key_1, val_1);
		request.addProperty(key_2, val_2);
		request.addProperty(key_3, val_3);
		request.addProperty(key_4, val_4);
		
		System.out.println(request + "*****Request******");
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		System.out.println(envelope + "****Envelop******");
		envelope.setOutputSoapObject(request);
		httpTransport = new AndroidHttpTransport(service_url);
		try {
			System.out.println("before call");
			httpTransport.call(soap_action, envelope);
			System.out.println("after call");
			System.out.println(envelope.getResponse());
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			System.out.println(response + "********responce*******");
			getServiceResponse(response.toString(), calling_service_name);
		} catch (Exception exception) {
			System.out.println(exception + " WS******Exception*******");
		}
	}

	/**
	 * web service for send notification to friend
	 * @param calling_service_name 
	 * @param soap_action
	 * @param target_namespace
	 * @param operation_name
	 * @param service_url
	 * @param key_1
	 * @param key_2
	 * @param key_3
	 * @param key_4
	 * @param val_1
	 * @param val_2
	 * @param val_3
	 * @param val_4
	 */
	public WebServiceConnection(String calling_service_name,String soap_action,String target_namespace,String operation_name,String service_url,String key_1,String key_2,String key_3,String key_4,String key_5,String val_1,String val_2,String val_3,String val_4,String val_5){
		request = new SoapObject(target_namespace, operation_name);
		request.addProperty(key_1, val_1);
		request.addProperty(key_2, val_2);
		request.addProperty(key_3, val_3);
		request.addProperty(key_4, val_4);
		request.addProperty(key_5, val_5);

		System.out.println(request + "*****Request******");
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		System.out.println(envelope + "****Envelop******");
		envelope.setOutputSoapObject(request);
		httpTransport = new AndroidHttpTransport(service_url);
		try {
			System.out.println("before call");
			httpTransport.call(soap_action, envelope);
			System.out.println("after call");
			System.out.println(envelope.getResponse());
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			System.out.println(response + "********responce*******");
			getServiceResponse(response.toString(), calling_service_name);
		} catch (Exception exception) {
			System.out.println(exception + " WS******Exception*******");
		}
	}
	
	
	public WebServiceConnection(String calling_service_name,String soap_action,String target_namespace,String operation_name,String service_url,String key_1,String key_2,String key_3,String key_4,String key_5,String key_6,String val_1,String val_2,String val_3,String val_4,String val_5,String val_6){
		request = new SoapObject(target_namespace, operation_name);
		request.addProperty(key_1, val_1);
		request.addProperty(key_2, val_2);
		request.addProperty(key_3, val_3);
		request.addProperty(key_4, val_4);
		request.addProperty(key_5, val_5);
		request.addProperty(key_6, val_6);

		System.out.println(request + "*****Request******");
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		System.out.println(envelope + "****Envelop******");
		envelope.setOutputSoapObject(request);
		httpTransport = new AndroidHttpTransport(service_url);
		try {
			System.out.println("before call");
			httpTransport.call(soap_action, envelope);
			System.out.println("after call");
			System.out.println(envelope.getResponse());
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			System.out.println(response + "********responce*******");
			getServiceResponse(response.toString(), calling_service_name);
		} catch (Exception exception) {
			System.out.println(exception + " WS******Exception*******");
		}
	}

	/**
	 * 
	 * @param web service for update user profile
	 * @param soap_action
	 * @param target_namespace
	 * @param operation_name
	 * @param service_url
	 * @param key_1
	 * @param key_2
	 * @param key_3
	 * @param key_4
	 * @param key_5
	 * @param val_1
	 * @param val_2
	 * @param val_3
	 * @param val_4
	 * @param val_5
	 */
	public WebServiceConnection(String calling_service_name,String soap_action,String target_namespace,String operation_name,String service_url,String key_1,String key_2,String key_3,String key_4,String key_5,String val_1,String val_2,String val_3,String val_4,byte[] val_5){
		request = new SoapObject(target_namespace, operation_name);
		request.addProperty(key_1, val_1);
		request.addProperty(key_2, val_2);
		request.addProperty(key_3, val_3);
		request.addProperty(key_4, val_4);
		request.addProperty(key_5, val_5);

		System.out.println(request + "*****Request******");
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;

		MarshalBase64 marshal=new MarshalBase64();
		marshal.register(envelope);

		System.out.println(envelope + "****Envelop******");
		envelope.setOutputSoapObject(request);
		httpTransport = new AndroidHttpTransport(service_url);
		try {
			System.out.println("before call");
			httpTransport.call(soap_action, envelope);
			System.out.println("after call");
			System.out.println(envelope.getResponse());
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			System.out.println(response + "********responce*******");
			getServiceResponse(response.toString(), calling_service_name);
		} catch (Exception exception) {
			System.out.println(exception + " WS******Exception*******");
		}
	}

	/**
	 * 
	 * @param webservice for uploading and updating the SOS image
	 * @param soap_action
	 * @param target_namespace
	 * @param operation_name
	 * @param service_url
	 * @param key_1
	 * @param key_2
	 * @param val_1
	 * @param val_2
	 */
	public WebServiceConnection(String calling_service_name,String soap_action,String target_namespace,String operation_name,String service_url,String key_1,String key_2,String key_3,String val_1,byte[] val_2,String val_3){
		request = new SoapObject(target_namespace, operation_name);
		request.addProperty(key_1, val_1);
		request.addProperty(key_2, val_2);
		request.addProperty(key_3, val_3);

		System.out.println(request + "*****Request******");
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;

		MarshalBase64 marshal=new MarshalBase64();
		marshal.register(envelope);

		System.out.println(envelope + "****Envelop******");
		envelope.setOutputSoapObject(request);
		httpTransport = new AndroidHttpTransport(service_url);
		try {
			System.out.println("before call");
			httpTransport.call(soap_action, envelope);
			System.out.println("after call");
			System.out.println(envelope.getResponse());
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			System.out.println(response + "********responce*******");
			getServiceResponse(response.toString(), calling_service_name);
		} catch (Exception exception) {
			System.out.println(exception + " WS******Exception*******");
		}
	}
	
	
	public WebServiceConnection(String calling_service_name,String soap_action,String target_namespace,String operation_name,String service_url,String key_1,String key_2,String val_1,byte[] val_2){
		request = new SoapObject(target_namespace, operation_name);
		request.addProperty(key_1, val_1);
		request.addProperty(key_2, val_2);

		System.out.println(request + "*****Request******");
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;

		MarshalBase64 marshal=new MarshalBase64();
		marshal.register(envelope);

		System.out.println(envelope + "****Envelop******");
		envelope.setOutputSoapObject(request);
		httpTransport = new AndroidHttpTransport(service_url);
		try {
			System.out.println("before call");
			httpTransport.call(soap_action, envelope);
			System.out.println("after call");
			System.out.println(envelope.getResponse());
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			System.out.println(response + "********responce*******");
			getServiceResponse(response.toString(), calling_service_name);
		} catch (Exception exception) {
			System.out.println(exception + " WS******Exception*******");
		}
	}

	/**
	 * 
	 * @param webservice for sending SOS request
	 * @param soap_action
	 * @param target_namespace
	 * @param operation_name
	 * @param service_url
	 * @param key_1
	 * @param key_2
	 * @param key_3
	 * @param key_4
	 * @param val_1
	 * @param val_2
	 * @param val_3
	 * @param val_4
	 */
	public WebServiceConnection(String calling_service_name,String soap_action,String target_namespace,String operation_name,String service_url,String key_1,String key_2,String key_3,String key_4,String val_1,String val_2,String val_3,byte[] val_4){
		request = new SoapObject(target_namespace, operation_name);

		request.addProperty(key_1, val_1);
		request.addProperty(key_2, val_2);
		request.addProperty(key_3, val_3);
		request.addProperty(key_4, val_4);

		System.out.println(request + "*****Request******");
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;

		MarshalBase64 marshal=new MarshalBase64();
		marshal.register(envelope);

		System.out.println(envelope + "****Envelop******");
		envelope.setOutputSoapObject(request);
		httpTransport = new AndroidHttpTransport(service_url);
		try {
			System.out.println("before call");
			httpTransport.call(soap_action, envelope);
			System.out.println("after call");
			System.out.println(envelope.getResponse());
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			System.out.println(response + "********responce*******");
			getServiceResponse(response.toString(), calling_service_name);
		} catch (Exception exception) {
			System.out.println(exception + " WS******Exception*******");
		}
	}



	public void getServiceResponse(String response,String serviceName){

		if(serviceName.equals("LOGIN_USER")){
			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
				System.out.println(responseList.getString("status"));
				System.out.println(responseList.getString("statusMsg"));
				System.out.println(responseList.getString("userid"));
				System.out.println(responseList.getString("isRegisteredOnWeb"));

				hashMap.put("status", responseList.getString("status"));
				hashMap.put("statusMsg", responseList.getString("statusMsg"));
				hashMap.put("userId", responseList.getString("userid"));
				hashMap.put("isRegisteredOnWeb", responseList.getString("isRegisteredOnWeb"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("REGISTER_USER")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
				System.out.println(responseList.getString("status"));
				System.out.println(responseList.getString("statusMsg"));
				System.out.println(responseList.getString("userid"));

				hashMap.put("status", responseList.getString("status"));
				hashMap.put("statusMsg", responseList.getString("statusMsg"));
				hashMap.put("userId", responseList.getString("userid"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("CHECK_USER_SUBSCRIPTION")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
				System.out.println(responseList.getString("Status"));
				System.out.println(responseList.getString("Message"));

				hashMap.put("status", responseList.getString("Status"));
				hashMap.put("statusMsg", responseList.getString("Message"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("SEND_SOS")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
				System.out.println(responseList.getString("Status"));
				System.out.println(responseList.getString("Message"));

				hashMap.put("status", responseList.getString("Status"));
				hashMap.put("statusMsg", responseList.getString("Message"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("SUBSCRIBE_USER")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
				System.out.println(responseList.getString("Status"));
				System.out.println(responseList.getString("Message"));

				hashMap.put("status", responseList.getString("Status"));
				hashMap.put("statusMsg", responseList.getString("Message"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("UPLOAD_SOS_IMAGE")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
				System.out.println(responseList.getString("Status"));
				System.out.println(responseList.getString("Message"));

				hashMap.put("status", responseList.getString("Status"));
				hashMap.put("statusMsg", responseList.getString("Message"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("GET_USER_SOS_IMAGE")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
				System.out.println(responseList.getString("Status"));
				System.out.println(responseList.getString("Message"));
				System.out.println(responseList.getString("Url"));

				hashMap.put("status", responseList.getString("Status"));
				hashMap.put("statusMsg", responseList.getString("Message"));
				hashMap.put("imageUrl", responseList.getString("Url"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("GET_FRIEND_UPDATED_LOCATION")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
				System.out.println(responseList.getString("Status"));
				System.out.println(responseList.getString("message"));
				System.out.println(responseList.getString("Latitude"));
				System.out.println(responseList.getString("Longitude"));

				hashMap.put("status", responseList.getString("Status"));
				hashMap.put("statusMsg", responseList.getString("message"));
				hashMap.put("latitude", responseList.getString("Latitude"));
				hashMap.put("longitude", responseList.getString("Longitude"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("FORGOT_PASSWORD")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
				System.out.println(responseList.getString("Status"));
				System.out.println(responseList.getString("Message"));

				hashMap.put("status", responseList.getString("Status"));
				hashMap.put("statusMsg", responseList.getString("Message"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("GET_CURRENT_LOCATION_NAME")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);

				hashMap.put("status", responseList.getString("Status"));
				hashMap.put("statusMsg", responseList.getString("message"));
				hashMap.put("location", responseList.getString("CurrentLocation"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("GET_SERVER_CONTACT_LIST")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("GET_ALL_LOCATIONS_NAMES")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("GET_ACCEPTED_FIND_ME_REQUESTS")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("VIEW_IN_PENDING_FRIEND_REQUESTS")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("GET_ACCEPTED_FOLLOW_YOU_REQUESTS")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("FOLLOWYOU_INCOMING_PENDING_REQUEST")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("GET_ALL_FRIENDS_LIST")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("FOLLOWYOU_OUTGOING_PENDING_REQUEST")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("FRIEND_ME_OUT_PENDING_REQUEST")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("FINDME_OUTGOING_PENDING_REQUEST")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("SEND_NEW_FRIEND_REQUEST")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
				System.out.println(responseList.getString("Status"));
				System.out.println(responseList.getString("Message"));

				hashMap.put("status", responseList.getString("Status"));
				hashMap.put("statusMsg", responseList.getString("Message"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("CHANGE_USER_STATE")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
				System.out.println(responseList.getString("Status"));
				System.out.println(responseList.getString("Message"));

				hashMap.put("status", responseList.getString("Status"));
				hashMap.put("statusMsg", responseList.getString("Message"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("USER_CURRENT_STATE")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
				System.out.println(responseList.getString("Status"));
				try{
					System.out.println(responseList.getString("Message"));
				}catch (Exception e) {
					e.printStackTrace();
				}
				hashMap.put("status", responseList.getString("Status"));
				try{
					hashMap.put("statusMsg", responseList.getString("Message"));
				}catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("SEND_NOTIFICATION")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
				System.out.println(responseList.getString("Status"));
				System.out.println(responseList.getString("Message"));

				hashMap.put("status", responseList.getString("Status"));
				hashMap.put("statusMsg", responseList.getString("Message"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("EDIT_USER_PROFILE")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
				System.out.println(responseList.getString("status"));
				System.out.println(responseList.getString("statusMsg"));

				hashMap.put("status", responseList.getString("status"));
				hashMap.put("statusMsg", responseList.getString("statusMsg"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("GET_LOGIN_USER_PROFILE")) {

			try {
				userProfileHashMap=new HashMap<String, Object>();
				responseList = new JSONObject(response);

				System.out.println(responseList.getString("FirstName"));
				System.out.println(responseList.getString("LastName"));
				System.out.println(responseList.getString("PhoneNumber"));
				System.out.println(responseList.getString("imagePath"));

				userProfileHashMap.put("FirstName", responseList.getString("FirstName"));
				userProfileHashMap.put("LastName", responseList.getString("LastName"));
				userProfileHashMap.put("PhoneNumber", responseList.getString("PhoneNumber"));
				try{
					userProfileHashMap.put("UserPicture", responseList.getString("imagePath"));
				}catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("FOLLOW_YOU_REQUEST")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
				System.out.println(responseList.getString("Status"));
				System.out.println(responseList.getString("Message"));

				hashMap.put("status", responseList.getString("Status"));
				hashMap.put("statusMsg", responseList.getString("Message"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("CHANGE_PASSWORD")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
				System.out.println(responseList.getString("Status"));
				System.out.println(responseList.getString("Message"));

				hashMap.put("status", responseList.getString("Status"));
				hashMap.put("statusMsg", responseList.getString("Message"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else if (serviceName.equals("FIND_ME_REQUEST")) {

			try {
				hashMap=new HashMap<String, String>();
				responseList = new JSONObject(response);
				System.out.println(responseList.getString("Status"));
				System.out.println(responseList.getString("Message"));

				hashMap.put("status", responseList.getString("Status"));
				hashMap.put("statusMsg", responseList.getString("Message"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public HashMap<String, String> serviceResponce(){
		return hashMap;
	}

	public HashMap<String, Object> userProfileServiceResponce(){
		return userProfileHashMap;
	}

	public JSONObject jsonResponse(){
		return responseList;
	}

}
