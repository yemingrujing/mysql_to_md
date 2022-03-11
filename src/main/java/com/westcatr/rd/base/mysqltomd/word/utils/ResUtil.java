package com.westcatr.rd.base.mysqltomd.word.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : ResUtil
 * @Package : com.westcatr.rd.base.mysqltomd.word.utils
 * @Description:
 * @date 2022/3/11 10:03
 **/
public class ResUtil {

	/**
	 * 生成下载文件，浏览器直接访问为下载文件
	 * @param request  请求对象
	 * @param data     数据流数组
	 * @param prefix   下载的文件名
	 * @param suffix   文件后缀
	 * @return 浏览器可以直接下载的文件流
	 */
	public static ResponseEntity<byte[]> getStreamData(
			HttpServletRequest request, byte[] data, String prefix, String suffix
	) {
		HttpHeaders headers = new HttpHeaders();
		prefix = StringUtils.isEmpty(prefix) ? "未命名" : prefix;
		suffix = suffix == null ? "" : suffix;
		try {
			String agent = request.getHeader("USER-AGENT");
			boolean isIE = null != agent, isMC = null != agent;
			isIE = isIE && (agent.indexOf("MSIE") != -1 || agent.indexOf("Trident") != -1);
			isMC = isMC && (agent.indexOf("Mozilla") != -1);
			prefix = isMC ? new String(prefix.getBytes("UTF-8"), "iso-8859-1") :
					(isIE ? java.net.URLEncoder.encode(prefix, "UTF8") : prefix);
			headers.setContentDispositionFormData("attachment", prefix + "." + suffix);
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			return new ResponseEntity<byte[]>(data, headers, HttpStatus.OK);
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage());
		}
	}
}
