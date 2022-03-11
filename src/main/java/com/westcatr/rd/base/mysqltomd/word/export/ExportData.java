package com.westcatr.rd.base.mysqltomd.word.export;

import com.westcatr.rd.base.mysqltomd.word.utils.SoMap;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : ExportData
 * @Package : com.westcatr.rd.base.mysqltomd.word.export
 * @Description:
 * @date 2022/3/11 9:53
 **/
public class ExportData {

	private IXDocReport report;
	private IContext context;

	/**
	 * 构造方法
	 * @param report
	 * @param context
	 */
	public ExportData(IXDocReport report, IContext context) {
		this.report = report;
		this.context = context;
	}

	/**
	 * 设置普通数据，包括基础数据类型，数组，试题对象
	 * 使用时，直接 ${key.k} 或者 [#list d as key]
	 * @param key   健
	 * @param value 值
	 */
	public void setData(String key, Object value) {
		context.put(key, value);
	}

	/**
	 * 设置表格数据，用来循环生成表格的 List 数据
	 * 使用时，直接 ${key.k}
	 * @param key   健
	 * @param maps List 集合
	 */
	public void setTable(String key, List<SoMap> maps) {
		FieldsMetadata metadata = report.getFieldsMetadata();
		metadata = metadata == null ? new FieldsMetadata() : metadata;
		SoMap map = maps.get(0);
		for (String kk : map.keySet()) {
			metadata.addFieldAsList(key + "." + kk);
		}
		report.setFieldsMetadata(metadata);
		context.put(key, maps);
	}

	/**
	 * 设置图片数据
	 * 使用时 直接在书签出 key
	 * @param key 健
	 * @param url 图片地址
	 */
	public void setImg(String key, String url) {
		FieldsMetadata metadata = report.getFieldsMetadata();
		metadata = metadata == null ? new FieldsMetadata() : metadata;
		metadata.addFieldAsImage(key);
		report.setFieldsMetadata(metadata);
		try (
				InputStream in = new ClassPathResource(url).getInputStream();
		) {
			IImageProvider img = new ByteArrayImageProvider(in);
			context.put(key, img);
		} catch (IOException ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	/**
	 * 获取文件流数据
	 * @return 文件流数组
	 */
	public byte[] getByteArr() {
		try (
				ByteArrayOutputStream out = new ByteArrayOutputStream();
		) {
			report.process(context, out);
			return out.toByteArray();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex.getMessage());
		}
	}
}
