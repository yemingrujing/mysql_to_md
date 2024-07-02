package com.westcatr.rd.base.mysqltomd.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author nanmu@taomz.com
 * @version V1.0
 * @title : PictureDemo
 * @Package : com.westcatr.rd.base.mysqltomd.image
 * @Description:
 * @date 2024/4/15 14:16
 **/
public class PictureDemo {

    /**
     * 将图片进行合成
     *
     * @param bigPath     主图图片路径
     * @param smallPath   商品图片路径
     * @param erweimaPath 二维码图片路径
     */
    public static final void overlapImage(String bigPath, String smallPath, String erweimaPath) {
        try {
            BufferedImage big = ImageIO.read(new File(bigPath));
            BufferedImage small = ImageIO.read(new File(smallPath));
            BufferedImage erweima = ImageIO.read(new File(erweimaPath));
          /*int width=2015;
          int height=1136;*/
            int width = 600;
            int height = 33;
            Image image = big.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage bufferedImage2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
            Graphics2D g = bufferedImage2.createGraphics();

         /* int x = 707;
          int y = 268;
          int x1 = 684;
          int y1 = 245;*/

            int x = 0;
            int y = 0;


            int x1 = 200;
            int y1 = 250;


            g.drawImage(image, 0, 0, null);
            // g.drawImage(small,x1-200, y1-250, 400, 200, null);
            //红包
            g.drawImage(small, x, y + 100, 50, 50, null);


            //g.drawImage(erweima, x1-575, y1+100, 596, 596, null);
            //二维码
            g.drawImage(erweima, x1 - 100, y1 + 80, 200, 200, null);

            Font font = new Font("黑体", Font.PLAIN, 20);
            g.setFont(font);
            g.setPaint(Color.WHITE);
            int numWidth = x + 200;
            int numHright = y + 25;
            int num = 0;
            // g.drawString("商品名称:" , numWidth,numHright);
            //g.drawString("测试棋牌室" , numWidth,numHright);

            String name = "测试测试测试测试试试";


            g.drawString(name, numWidth, numHright);

      /*    g.setPaint(Color.DARK_GRAY);
          Font font1=new Font("宋体",Font.BOLD , 15);
          g.setFont(font1);
          numWidth=numWidth-25;
          g.drawString("江苏美联信息科技有限公司" , numWidth,numHright+280); */
          /*num += 50;
          Font font2=new Font("宋体",Font.PLAIN , 40);
          g.setFont(font2);
          g.setPaint(Color.DARK_GRAY);
          g.drawString("原产地:", numWidth, numHright+num);
          num += 50;
          g.drawString("配送方式:",numWidth, numHright+num); */
            g.dispose();
            ImageIO.write(bufferedImage2, "jpg", new File("D:/file/4.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final void main(String[] args) {
        overlapImage("D:/file/111.png", "D:/file/111.png", "D:/file/111.png");
    }
}
