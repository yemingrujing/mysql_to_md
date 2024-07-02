package com.westcatr.rd.base.mysqltomd.svg;

import lombok.Data;

import java.util.List;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : SvgElementDTO
 * @Package : com.westcatr.rd.base.mysqltomd.svg
 * @Description:
 * @date 2024/7/2 13:49
 **/
@Data
public class SvgElementDTO {

    private String tagName;

    private String id;

    private String dataName;

    private String transform;

    private String textContent;

    private List<SvgElementDTO> childElementList;
}
