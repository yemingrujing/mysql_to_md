package com.westcatr.rd.base.mysqltomd.svg;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.svg2svg.SVGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.svg.SVGDocument;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : SvgUtil
 * @Package : com.westcatr.rd.base.mysqltomd.svg
 * @Description:
 * @date 2024/7/1 13:53
 **/
public class SvgUtil {

    public static void main(String[] args) throws IOException {
        File svgFile = new File("D:\\file\\svg\\111.svg");
        // 创建URL对象
        String svgURL = svgFile.toURI().toString();

        // 使用Batik的XMLResourceDescriptor获取DOM支持
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        // 创建SAXSVGDocumentFactory实例
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        // 通过SAX方式读取SVG文件
        SVGDocument svgDoc = f.createSVGDocument(svgURL);

        SvgElementDTO dto = JSON.parseObject("{" +
                "    \"tagName\": \"g\"," +
                "    \"id\": \"组_410\"," +
                "    \"dataName\": \"组_410\"," +
                "    \"transform\": \"translate(3689.096 4439.313)\"," +
                "    \"textContent\": \"\"," +
                "    \"childElementList\": [{" +
                "            \"tagName\": \"path\"," +
                "            \"id\": \"路径_7-139\"," +
                "            \"dataName\": \"路径_7\"," +
                "            \"transform\": \"translate(-2345.533 -1587.613)\"" +
                "        }, {" +
                "            \"tagName\": \"text\"," +
                "            \"id\": \"_3B88\"," +
                "            \"dataName\": \"3B88\"," +
                "            \"transform\": \"translate(294.404 158.134)\"," +
                "            \"childElementList\": [{" +
                "                    \"tagName\": \"tspan\"," +
                "                    \"textContent\": \"3B88\"" +
                "                }" +
                "            ]" +
                "        }" +
                "    ]" +
                "}", SvgElementDTO.class);

        Element root = svgDoc.getRootElement();
        rootElement(svgDoc, root, "_3号厅消费品展区", dto);

        writeSVG(svgDoc, "D:\\file\\svg\\222.svg");
    }

    private static void rootElement(SVGDocument svgDoc, Element element, String idName, SvgElementDTO dto) {
        if (Objects.nonNull(element)) {
            NodeList nodeList = element.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i) instanceof Element) {
                    Element childElement = (Element) nodeList.item(i);
                    if (childElement.getAttribute("id").equals(idName)) {
                        childElement.appendChild(readLastElement(childElement, dto));
                    }
                }
            }
        }
    }

    private static Element readLastElement(Element element, SvgElementDTO dto) {
        Element lastElement = null;
        if (Objects.nonNull(element)) {
            NodeList nodeList = element.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i) instanceof Element) {
                    Element childElement = (Element) nodeList.item(i);
                    lastElement = (Element) childElement.cloneNode(true);
                }
            }
        }
        if (Objects.nonNull(dto) && lastElement.getTagName().equals(dto.getTagName())) {
            setElementAttribute(lastElement, Arrays.asList(dto));
        }
        return lastElement;
    }

    private static void setElementAttribute(Element element, List<SvgElementDTO> dtoList) {
        if (CollUtil.isNotEmpty(dtoList)) {
            SvgElementDTO dto = dtoList.stream().filter(item -> item.getTagName().equals(element.getTagName())).findFirst().orElse(null);
            if (Objects.nonNull(dto)) {
                if (StrUtil.isNotBlank(dto.getId())) {
                    element.setAttribute("id", dto.getId());
                }
                if (StrUtil.isNotBlank(dto.getDataName())) {
                    element.setAttribute("data-name", dto.getDataName());
                }
                if (StrUtil.isNotBlank(dto.getTransform())) {
                    element.setAttribute("transform", dto.getTransform());
                }
                if (StrUtil.isNotBlank(dto.getTextContent())) {
                    element.setTextContent(dto.getTextContent());
                }
                NodeList nn = element.getChildNodes();
                for (int i = 0; i < nn.getLength(); i++) {
                    if (nn.item(i) instanceof Element) {
                        Element childElement = (Element) nn.item(i);
                        setElementAttribute(childElement, dto.getChildElementList());
                    }
                }
            }
        }
    }

    private static void writeSVG(SVGDocument svgDoc, String outputPath) {
        TranscoderInput input = new TranscoderInput(svgDoc);
        SVGTranscoder t = new SVGTranscoder();
        try (OutputStream os = new FileOutputStream(outputPath)) {
            OutputStreamWriter writer = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            TranscoderOutput output = new TranscoderOutput(writer);
            t.transcode(input, output);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TranscoderException e) {
            throw new RuntimeException(e);
        }
    }
}
