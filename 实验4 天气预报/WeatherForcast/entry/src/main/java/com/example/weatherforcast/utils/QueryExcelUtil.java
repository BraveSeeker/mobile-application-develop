package com.example.weatherforcast.utils;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import ohos.app.Context;
import ohos.global.resource.Resource;
import ohos.hiviewdfx.HiLog;

import java.io.IOException;
import java.util.Vector;

import static com.example.weatherforcast.slice.MainAbilitySlice.LABEL_LOG;


public class QueryExcelUtil {
    public static Vector<Cell[]> get_city_response(Context context,String cityChineseName) {
        Vector<Cell[]> city_response = new Vector<>();
        if (cityChineseName.length() == 0) return city_response;
        try {
            // 打开excel文件
            Workbook wb = null;
            Resource resource = context.getResourceManager().getRawFileEntry("entry/resources/rawfile/city_list.xls").openRawFile();
            wb = Workbook.getWorkbook(resource);
            int sheetSize = wb.getNumberOfSheets();
            if (sheetSize == 0) {
                HiLog.info(LABEL_LOG, "Excel中没有工作表");
            }
            // 获取excel的第一个sheet
            Sheet sheet = wb.getSheet(1);
            int rowTotal = sheet.getRows();

            // 遍历单元格获取数据
            for (int i = 0; i < rowTotal; i++) {
                Cell[] cells = sheet.getRow(i);
                if (containSameCity(cells, cityChineseName)) {
                    city_response.add(cells);
//                   break;
                }
            }
        } catch (IOException | BiffException e) {
            HiLog.debug(LABEL_LOG, "异常：IOException:" + e);
        }
        return city_response;
    }

    private static boolean containSameCity(Cell[] cell, String city_name) {
        return cell[1].getContents().contains(city_name) || cell[2].getContents().contains(city_name);
    }
}
