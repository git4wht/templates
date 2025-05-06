

    function create(input) {
        var XSSFWorkbook = Packages.org.apache.poi.xssf.usermodel.XSSFWorkbook;
        var workbook = new XSSFWorkbook(input.templateFile);

        // 元シート
        var templateSheet = workbook.createSheet("template");
        fillTemplate(templateSheet, workbook);

        // 先シート
        var targetSheet = workbook.createSheet("print_sheet");

        // 領域範囲をコピー
        var fromRow = 0;
        var toRow = 49;
        var fromCol = 0;
        var toCol = 7;
        var pageHeight = toRow - fromRow + 1;

        // バッチコピーXX部
        for (var i = 0; i < input.data.length; i++) {
            var targetStartRow = i * pageHeight;
            copyRegion(workbook, templateSheet, targetSheet, fromRow, toRow, fromCol, toCol, targetStartRow);

            // 改ページを追加する
            targetSheet.setRowBreak(targetStartRow + pageHeight - 1);
        }

        // 印刷設定のコピー
        copyPrintSetup(templateSheet, targetSheet);

        // テンプレートシートを削除
        var templateIndex = workbook.getSheetIndex(templateSheet);
        workbook.removeSheetAt(templateIndex);

        // 保存结果
        try (var out = new FileOutputStream(input.printFile)) {
            workbook.write(out);
        }

        workbook.close();
    }

    function copyRegion(wb, src, dest, fromRow, toRow, fromCol, toCol, targetRowOffset) {
        var CellStyle = Packages.org.apache.poi.ss.usermodel.CellStyle;
        var CellRangeAddress = Packages.org.apache.poi.ss.util.CellRangeAddress;
        for (var r = fromRow; r <= toRow; r++) {
            var srcRow = src.getRow(r);
            var destRow = dest.createRow(r - fromRow + targetRowOffset);
            if (srcRow != null) {
                destRow.setHeight(srcRow.getHeight());
                for (var c = fromCol; c <= toCol; c++) {
                    var srcCell = srcRow.getCell(c);
                    var destCell = destRow.createCell(c);
                    if (srcCell != null) {
                        var newStyle = wb.createCellStyle();
                        newStyle.cloneStyleFrom(srcCell.getCellStyle());
                        destCell.setCellStyle(newStyle);

                        switch (srcCell.getCellType()) {
                            case CellStyle.STRING:
                                destCell.setCellValue(srcCell.getStringCellValue());
                                break;
                            case CellStyle.NUMERIC:
                                destCell.setCellValue(srcCell.getNumericCellValue());
                                break;
                            case CellStyle.BOOLEAN:
                                destCell.setCellValue(srcCell.getBooleanCellValue());
                                break;
                            case CellStyle.FORMULA:
                                destCell.setCellFormula(srcCell.getCellFormula());
                                break;
                            case CellStyle.BLANK:
                                destCell.setBlank();
                                break;
                        }
                    }
                }
            }
        }

        // セルを結合してコピーする（オフセットが必要）
        for (var i = 0; i < src.getNumMergedRegions(); i++) {
            var region = src.getMergedRegion(i);
            if (region.getFirstRow() >= fromRow && region.getLastRow() <= toRow &&
                region.getFirstColumn() >= fromCol && region.getLastColumn() <= toCol) {
                var firstRow = region.getFirstRow() - fromRow + targetRowOffset;
                var lastRow = region.getLastRow() - fromRow + targetRowOffset;
                var newRegion = new CellRangeAddress(firstRow, lastRow, region.getFirstColumn(), region.getLastColumn());
                dest.addMergedRegion(newRegion);
            }
        }

        // 列幅をコピー
        for (var col = fromCol; col <= toCol; col++) {
            dest.setColumnWidth(col, src.getColumnWidth(col));
        }
    }

    function copyPrintSetup(src, dest) {
        var Sheet = Packages.org.apache.poi.ss.usermodel.Sheet;
        var psSrc = src.getPrintSetup();
        var psDest = dest.getPrintSetup();
        psDest.setPaperSize(psSrc.getPaperSize());
        psDest.setLandscape(psSrc.getLandscape());
        psDest.setFitWidth(psSrc.getFitWidth());
        psDest.setFitHeight(psSrc.getFitHeight());
        dest.setAutobreaks(src.getAutobreaks());

        dest.setMargin(Sheet.LeftMargin, src.getMargin(Sheet.LeftMargin));
        dest.setMargin(Sheet.RightMargin, src.getMargin(Sheet.RightMargin));
        dest.setMargin(Sheet.TopMargin, src.getMargin(Sheet.TopMargin));
        dest.setMargin(Sheet.BottomMargin, src.getMargin(Sheet.BottomMargin));

        dest.getHeader().setCenter(src.getHeader().getCenter());
        dest.getFooter().setRight(src.getFooter().getRight());
    }
