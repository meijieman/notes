package com.hongfans.generatevaluefiles;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by zhy on 15/5/3.
 */
public class GenerateValueFiles {

    private int mBaseW; // 基准宽
    private int mBaseH; // 基准高

    private String dirStr = "./res"; // 生成的文件目录

    private final static String WTemplate = "<dimen name=\"x{0}\">{1}px</dimen>\n";
    private final static String HTemplate = "<dimen name=\"y{0}\">{1}px</dimen>\n";

    /**
     * {0}-HEIGHT
     */
    private final static String VALUE_TEMPLATE = "values-{0}x{1}";

    //    private static final String SUPPORT_DIMENSION = "320,480;480,640;400,648;480,722;480,800;600,800;480,806;480,854;480,960;540,960;640,960;600,1024;768,1024;720,1080;640,1136;480,1148;400,1180;720,1184;720,1196;480,1280;720,1280;800,1280;1080,1774;1080,1812;1080,1920;1536,2048;1440,2560;";
    private static final String SUPPORT_DIMENSION = "540,960;600,1024;720,1280;800,1280;1080,1774;1080,1812;1080,1920;1536,2048;1440,2560;";

    private String mSupportStr = SUPPORT_DIMENSION;

    public GenerateValueFiles(int baseX, int baseY, String supportStr) {
        mBaseW = baseX;
        mBaseH = baseY;

        if (!mSupportStr.contains(baseX + "," + baseY)) {
            mSupportStr += baseX + "," + baseY + ";";
        }

        mSupportStr += validateInput(supportStr);

        print("supportStr " + supportStr);

        File dir = new File(dirStr);
        if (!dir.exists()) {
            dir.mkdir();
        }
        print("生成目录 " + dir.getAbsoluteFile());
    }

    public static void main(String[] args) {
        int baseW = 640;// 640x1136
        int baseH = 1136;
        String addition = "";
        try {
            // 命令行输入参数
            if (args.length >= 3) {
                baseW = Integer.parseInt(args[0]);
                baseH = Integer.parseInt(args[1]);
                addition = args[2];
            } else if (args.length >= 2) {
                baseW = Integer.parseInt(args[0]);
                baseH = Integer.parseInt(args[1]);
            } else if (args.length >= 1) {
                addition = args[0];
            }
        } catch (NumberFormatException e) {
            printErr("right input params : java -jar xxx.jar width height w,h_w,h_..._w,h;");
            e.printStackTrace();
            System.exit(-1);
        }

        new GenerateValueFiles(baseW, baseH, addition).generate();
    }

    /**
     * @param supportStr w,h_...w,h;
     * @return
     */
    private String validateInput(String supportStr) {
        StringBuilder sb = new StringBuilder();
        String[] values = supportStr.split("_");
        int w;
        int h;
        String[] wh;
        for (String val : values) {
            try {
                if (val == null || val.trim().length() == 0) {
                    continue;
                }

                wh = val.split(",");
                w = Integer.parseInt(wh[0]);
                h = Integer.parseInt(wh[1]);
            } catch (Exception e) {
                printErr("skip invalidate params : w,h = " + val);
                continue;
            }
            sb.append(w + "," + h + ";");
        }

        return sb.toString();
    }

    private void generate() {
        String[] vals = mSupportStr.split(";");
        for (String val : vals) {
            String[] wh = val.split(",");
            generateXmlFile(Integer.parseInt(wh[0]), Integer.parseInt(wh[1]));
        }

    }

    private void generateXmlFile(int w, int h) {
        StringBuilder sbForWidth = new StringBuilder();
        sbForWidth.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\t<resources>\n");
        float cellw = w * 1.0f / mBaseW;
        print("generateXmlFile cellw " + cellw);
        for (int i = 1; i < mBaseW; i++) {
            sbForWidth.append(WTemplate.replace("{0}", i + "").replace("{1}", change(cellw * i) + ""));
        }
        sbForWidth.append(WTemplate.replace("{0}", mBaseW + "").replace("{1}", w + ""));
        sbForWidth.append("</resources>");

        StringBuilder sbForHeight = new StringBuilder();
        sbForHeight.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n\t<resources>\n");
        float cellh = h * 1.0f / mBaseH;
        print("height : " + h + "," + mBaseH + "," + cellh);
        for (int i = 1; i < mBaseH; i++) {
            sbForHeight.append(HTemplate.replace("{0}", i + "").replace("{1}", change(cellh * i) + ""));
        }
        sbForHeight.append(HTemplate.replace("{0}", mBaseH + "").replace("{1}", h + ""));
        sbForHeight.append("</resources>");

        File fileDir = new File(dirStr + File.separator + VALUE_TEMPLATE.replace("{0}", h + "").replace("{1}", w + ""));
        fileDir.mkdir();

        File layxFile = new File(fileDir.getAbsolutePath(), "lay_x.xml");
//        File layxFile = new File(fileDir.getAbsolutePath(), "dimens.xml");
        File layyFile = new File(fileDir.getAbsolutePath(), "lay_y.xml");
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(layxFile));
            pw.print(sbForWidth.toString());
            pw.close();
            pw = new PrintWriter(new FileOutputStream(layyFile));
            pw.print(sbForHeight.toString());
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 保留两位小数
    public static float change(float a) {
        int temp = (int) (a * 100);
        return temp / 100f;
    }


    public static void print(String msg) {

        System.out.println(msg);
    }

    public static void printErr(String msg) {
        System.err.println(msg);
    }
}
