package com.westcatr.rd.base.mysqltomd.svg;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : SvgPngConverter
 * @Package : com.westcatr.rd.base.mysqltomd.svg
 * @Description:
 * @date 2024/7/1 14:04
 **/
@Slf4j
public class SvgPngConverter {

    /**
     * 将svg字符串转换为png
     *
     * @param svgCode     svg代码
     * @param pngFilePath 保存的路径
     * @throws IOException io错误
     */
    public static void convertToPng(String svgCode, String pngFilePath) throws IOException {

        File file = new File(pngFilePath);

        FileOutputStream outputStream = null;
        try {
            file.createNewFile();
            outputStream = new FileOutputStream(file);
            convertToPng(svgCode, outputStream);
        } finally {
            IoUtil.close(outputStream);
        }
    }

    /**
     * 将svgCode转换成png文件，直接输出到流中
     *
     * @param svgCode      svg代码
     * @param outputStream 输出流
     * @throws TranscoderException 异常
     * @throws IOException         io异常
     */
    public static void convertToPng(String svgCode, OutputStream outputStream) {
        try {
            byte[] bytes = svgCode.getBytes("utf-8");
            PNGTranscoder t = new PNGTranscoder();
            TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(bytes));
            TranscoderOutput output = new TranscoderOutput(outputStream);
            t.transcode(input, output);
            outputStream.flush();
        } catch (TranscoderException e) {
            log.error("svg转png失败", e);
        } catch (UnsupportedEncodingException e) {
            log.error("svg转png失败", e);
        } catch (IOException e) {
            log.error("svg转png失败", e);
        } finally {
            IoUtil.close(outputStream);
        }
    }
}
