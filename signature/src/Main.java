import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

// This import can be found easily in Spring

import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Main {
	public static void main(String[] args) {
		String SDK_KEY = "";
		String SDK_SECRET = "";
		System.out.println(getZoomSignature(SDK_KEY, SDK_SECRET));
	}

	public static String getZoomSignature(String sdkKey, String sdkSecret) {
		if (sdkKey == null || sdkSecret == null) {
			return null;
		} else {
			long iat = Math.round(new Date().getTime() / (float) 1000) - 30;
			long exp = iat + 60 * 60 * 2;

			JSONObject header = new JSONObject();
			header.put("alg", "HS256");
			header.put("typ", "JWT");

			JSONObject payload = new JSONObject();
			payload.put("sdkKey", sdkKey);
			payload.put("mn", id);  // Meeting number need to join.
			payload.put("role", 0); // 0 for joining the meeting while 1 for starting the meeting.
			payload.put("iat", iat);
			payload.put("exp", exp);
			payload.put("appKey", sdkKey);
			payload.put("tokenExp", exp);

			final String base64Payload = encode(payload.toString().getBytes(StandardCharsets.UTF_8));
			final String base64header = encode(header.toString().getBytes(StandardCharsets.UTF_8));
			final String signature = hmacSha256(base64header + "." + base64Payload, sdkSecret);

			return base64header + "." + base64Payload + "." + signature;
		}

	}

	public static String hmacSha256(String data, String secret) {
		try {
			byte[] hash = secret.getBytes(StandardCharsets.UTF_8);
			Mac sha256Hmac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secretKey = new SecretKeySpec(hash, "HmacSHA256");
			sha256Hmac.init(secretKey);
			byte[] signedBytes = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
			return encode(signedBytes);
		} catch (NoSuchAlgorithmException | InvalidKeyException ex) {
			/*
			 * Do the Logging
			 */
		}
		return null;
	}

	public static String encode(byte[] bytes) {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}
}