package com.westcatr.rd.base.mysqltomd.word.utils;

import com.westcatr.rd.base.mysqltomd.word.export.ExportData;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : WordUtil
 * @Package : com.westcatr.rd.base.mysqltomd.word.utils
 * @Description:
 * @date 2022/3/11 10:01
 **/
public class WordUtil {

	/**
	 * 获取 Word 模板的两个操作对象 IXDocReport 和 IContext
	 * @param url 模板相对于类路径的地址
	 * @return 模板数据对象
	 */
	public static ExportData createExportData(String url) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			IXDocReport report = createReport(url);
			IContext context = report.createContext();
			return new ExportData(report, context);
		} catch (XDocReportException ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}

	/**
	 * 加载模板的方法，主要是指定模板的路径和选择渲染数据的模板
	 * @param url 模板相对于类路径的地址
	 * @return word 文档操作类
	 */
	private static IXDocReport createReport(String url) {
		try (
				InputStream in = new ClassPathResource(url).getInputStream();
		) {
			return XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Freemarker);
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}
}
