package com.westcatr.rd.base.mysqltomd;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.westcatr.rd.base.mysqltomd.dao.mapper.IMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author xieshuang
 * @date 2020-05-31 18:46
 */
@Service
public class TableServiceImpl implements TableService {

    @Autowired
    private IMapper iMapper;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${exclude.table:}")
    private String excludeTable;
    @Value("${appoint.table:}")
    private String appointTable;
    @Override
    public List<TableInfo> getTableList() {
        String[] split = url.split("\\?");
        String s = split[0];
        String[] split1 = s.split("/");
        String sql = "SELECT\n" +
               "\tTABLE_NAME AS tableName,\n" +
               "\tTABLE_COMMENT AS tableComment \n" +
               "FROM\n" +
               "\tinformation_schema.TABLES \n" +
               "WHERE\n" +
               "\ttable_schema='" + split1[split1.length-1] + "'";
        List<JSONObject> jsonObjects = iMapper.selectList(sql);
        List<TableInfo> list = new ArrayList<>(jsonObjects.size());
        for (JSONObject jsonObject : jsonObjects) {
            list.add(JSON.toJavaObject(jsonObject, TableInfo.class));
        }
        if (excludeTable != null && !"".equals(excludeTable)){
            ArrayList<String> strings = new ArrayList<>(Arrays.asList(excludeTable.split(",")));
            list.removeIf(temp -> strings.contains(temp.getTableName()));
        }

        if (appointTable != null && !"".equals(appointTable)){
            ArrayList<String> strings = new ArrayList<>(Arrays.asList(appointTable.split(",")));
            list.removeIf(temp -> !strings.contains(temp.getTableName()));
        }
        return list;
    }

    @Override
    public List<FieldInfo> getFieldInfoList(String tableName) {
        String[] split = url.split("\\?");
        String s = split[0];
        String[] split1 = s.split("/");
        String sql = "SELECT\n" +
                "\tORDINAL_POSITION number,\n" +
                "\tCOLUMN_NAME fieldName,\n" +
                "\tDATA_TYPE fieldType,\n" +
                "\tcolumn_type fieldDetails,\n" +
                "CASE\n" +
                "\t\t\n" +
                "\t\tWHEN CHARACTER_MAXIMUM_LENGTH IS NULL THEN\n" +
                "\t\t(\n" +
                "\t\tCASE\n" +
                "\t\t\t\t\n" +
                "\t\t\t\tWHEN LOCATE( 'decimal', column_type )> 0 THEN\n" +
                "\t\t\t\tCONCAT( NUMERIC_PRECISION, ',', NUMERIC_SCALE )  \n" +
                "\t\t\t\tWHEN LOCATE( 'smallint', column_type )> 0 THEN\n" +
                "\t\t\t\tSUBSTRING_INDEX( SUBSTRING_INDEX( column_type, '(', - 1 ), ')', 1 )  \n" +
                "\t\t\t\tWHEN LOCATE( 'tinyint', column_type )> 0 THEN\n" +
                "\t\t\t\tSUBSTRING_INDEX( SUBSTRING_INDEX( column_type, '(', - 1 ), ')', 1 )  \n" +
                "\t\t\t\tWHEN LOCATE( 'bigint', column_type )> 0 THEN\n" +
                "\t\t\t\tSUBSTRING_INDEX( SUBSTRING_INDEX( column_type, '(', - 1 ), ')', 1 )  \n" +
                "\t\t\t\tWHEN LOCATE( 'int', column_type )> 0 THEN\n" +
                "\t\t\t\tSUBSTRING_INDEX( SUBSTRING_INDEX( column_type, '(', - 1 ), ')', 1 )  ELSE 0 \n" +
                "\t\t\tEND \n" +
                "\t\t\t) ELSE CHARACTER_MAXIMUM_LENGTH \n" +
                "\t\tEND fieldLength,\n" +
                "\t'' identity,\n" +
                "IF\n" +
                "\t( COLUMN_KEY = 'PRI', '1', '' ) f_key,\n" +
                "\t( CASE IS_NULLABLE WHEN 'YES' THEN '是' ELSE '不是' END ) AS isEmpty,\n" +
                "\tIFNULL(COLUMN_DEFAULT, '') defaultValue,\n" +
                "CASE\n" +
                "\t\t\n" +
                "\t\tWHEN COLUMN_COMMENT = '' THEN\n" +
                "\t\tCOLUMN_NAME ELSE COLUMN_COMMENT \n" +
                "\tEND fieldExplain \n" +
                "FROM\n" +
                "\t( SELECT * FROM information_schema.`COLUMNS` t1 WHERE TABLE_SCHEMA = '" + split1[split1.length-1] + "'\n" +
                "\tAND TABLE_NAME = '" + tableName + "') t2\n" +
                "ORDER BY\n" +
                "\tnumber";
        List<JSONObject> jsonObjects = iMapper.selectList(sql);
        List<FieldInfo> list = new ArrayList<>(jsonObjects.size());
        for (JSONObject jsonObject : jsonObjects) {
            if ("id".equals(jsonObject.getString("fieldName"))){
                jsonObject.put("fieldExplain", "主键");
            }
            if ("create_time".equals(jsonObject.getString("fieldName"))){
                jsonObject.put("fieldExplain", "创建时间");
            }
            if ("update_time".equals(jsonObject.getString("fieldName"))){
                jsonObject.put("fieldExplain", "更新时间");
            }
            list.add(JSON.toJavaObject(jsonObject, FieldInfo.class));
        }
        return list;
    }
}
