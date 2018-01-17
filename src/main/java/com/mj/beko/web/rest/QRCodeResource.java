package com.mj.beko.web.rest;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class QRCodeResource {

    //二维码的图片宽和高
    private static final int IMAGE_WIDTH = 260;
    private static final int IMAGE_HEIGHT = 260;
    //二维码的图片格式
    private static final String FORMAT = "jpg";
    private static final String URL_PRE = "http://";
    private static final String URL_SUF = "/api/QRCode/parse?stationName=";
    private String localhostAddress;
    private String port=":8088";


    /*
    * 根据stationName生成二维码图片
    */
    @RequestMapping("/QRCode/get")
    public void generateQRCode(String stationName, HttpServletResponse response) {
        try {
            if (localhostAddress == null||localhostAddress.equals("")) {
                InetAddress inetAddress = InetAddress.getLocalHost();
                localhostAddress = inetAddress.getHostAddress();
            }
            String text = URL_PRE+localhostAddress+port+URL_SUF+stationName;
            Hashtable hints = new Hashtable();
            //内容所使用编码
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, IMAGE_WIDTH, IMAGE_HEIGHT, hints);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    //二维码图片为黑白两色
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            response.setContentType("image/jpg");
            ImageIO.write(image, FORMAT, response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
    * 扫描二维码获取相关信息
    */
    @RequestMapping("/QRCode/parse")
    public Map parseQRCode(String stationName) {
        Map<String, String> result = new HashMap<>();
        //todo 具体返回信息待完善
        result.put("stationName", stationName);
        result.put("information", "XXXXX");
        return result;
    }
}
