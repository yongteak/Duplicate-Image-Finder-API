package work;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtility {
    public static final String IMAGE_WIDTH = "IMAGE_WIDTH";
    public static final String IMAGE_HEIGHT = "IMAGE_HEIGHT";

    /**
     * Grayscale 변환
     *
     * @param image  이미지 객체
     * @param width  리사이즈 이미지 너비
     * @param height 리사이즈 이미지 높이
     * @return
     */
    public static BufferedImage resizeAndGreyImage(BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(image, 0, 0, width, height, null);
        graphics.dispose();
        graphics.setComposite(AlphaComposite.Src);

        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        return resizedImage;
    }

    /**
     * Grayscale 객체에서 필셀 정보 추출
     *
     * @param image
     * @return
     */
    public static int[][] getGreyPixelArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] greyPixelArray = new int[height][width];
        for (int y = 0; y < image.getHeight(); y++)
            for (int x = 0; x < image.getWidth(); x++)
                greyPixelArray[y][x] = image.getRGB(x, y) & 0xFF;
        return greyPixelArray;
    }

    /**
     * 이진 해시 생성
     *
     * @param colorArray
     * @return
     */
    public static String generateImageHash(int[][] colorArray) {
        StringBuffer sb = new StringBuffer();
        int height = colorArray.length;
        int width = colorArray[0].length - 1;
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < height; x++) {
                sb.append(colorArray[y][x] < colorArray[y][x + 1] ? "1" : "0");
            }
        }
        return sb.toString();
    }

    /**
     * 이미지 파일에서 해시정보 생성
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String getImageHash(File filePath) throws IOException {
        // 이미지 객체 로드
        BufferedImage loadedImage = readFromPath(filePath);
        // greyscale 이미지 변환
        BufferedImage greyImage = resizeAndGreyImage(loadedImage, 9, 8);
        // 픽셀 정보 추출
        int[][] pixels = getGreyPixelArray(greyImage);
        // 해시정보 생성
        return generateImageHash(pixels);
    }

    /**
     * 이미지 파일 로드
     * @param filePath
     * @return
     * @throws IOException
     */
    private static BufferedImage readFromPath(File filePath) throws IOException {
        try {
            BufferedImage loadImage = ImageIO.read(filePath);
            return loadImage;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw ioe;
        }
    }
}
