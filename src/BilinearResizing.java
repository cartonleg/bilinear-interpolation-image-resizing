import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BilinearResizing {

    public static int[][][] resize(int[][][] originalImg, int newH, int newW) {
        int oldH = originalImg.length;
        int oldW = originalImg[0].length;
        int channels = originalImg[0][0].length;

        int[][][] resized = new int[newH][newW][channels];

        double wScaleFactor = (newW != 0) ? (double) oldW / newW : 0;
        double hScaleFactor = (newH != 0) ? (double) oldH / newH : 0;

        for (int i = 0; i < newH; i++) {
            for (int j = 0; j < newW; j++) {
                double x = i * hScaleFactor;
                double y = j * wScaleFactor;


                int xFloor = (int) Math.floor(x);
                int xCeil = Math.min(oldH - 1, (int) Math.ceil(x));
                int yFloor = (int) Math.floor(y);
                int yCeil = Math.min(oldW - 1, (int) Math.ceil(y));

                double[] q = new double[channels];

                if (xCeil == xFloor && yCeil == yFloor) {
                    for (int c = 0; c < channels; c++) {
                        q[c] = originalImg[xFloor][yFloor][c];
                    }
                }
                else if (xCeil == xFloor) {
                    for (int c = 0; c < channels; c++) {
                        double q1 = originalImg[xFloor][yFloor][c];
                        double q2 = originalImg[xFloor][yCeil][c];
                        q[c] = q1 * (yCeil - y) + q2 * (y - yFloor);
                    }
                }
                else if (yCeil == yFloor) {
                    for (int c = 0; c < channels; c++) {
                        double q1 = originalImg[xFloor][yFloor][c];
                        double q2 = originalImg[xCeil][yFloor][c];
                        q[c] = q1 * (xCeil - x) + q2 * (x - xFloor);
                    }
                }
                else {
                    for (int c = 0; c < channels; c++) {
                        double v1 = originalImg[xFloor][yFloor][c];
                        double v2 = originalImg[xCeil][yFloor][c];
                        double v3 = originalImg[xFloor][yCeil][c];
                        double v4 = originalImg[xCeil][yCeil][c];

                        double q1 = v1 * (xCeil - x) + v2 * (x - xFloor);
                        double q2 = v3 * (xCeil - x) + v4 * (x - xFloor);
                        q[c] = q1 * (yCeil - y) + q2 * (y - yFloor);
                    }
                }

                for (int c = 0; c < channels; c++) {
                    resized[i][j][c] = Math.max(0, Math.min(255, (int) Math.round(q[c])));
                }
            }
        }

        return resized;
    }

    public static int[][][] loadBmpToArray(String filePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(filePath));
        int height = image.getHeight();
        int width = image.getWidth();

        int[][][] imageArray = new int[height][width][3];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = image.getRGB(j, i);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                imageArray[i][j][0] = red;
                imageArray[i][j][1] = green;
                imageArray[i][j][2] = blue;
            }
        }

        return imageArray;
    }

    public static void saveArrayToBmp(int[][][] imageArray, String outputPath) throws IOException {
        int height = imageArray.length;
        int width = imageArray[0].length;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int red = Math.max(0, Math.min(255, imageArray[i][j][0]));
                int green = Math.max(0, Math.min(255, imageArray[i][j][1]));
                int blue = Math.max(0, Math.min(255, imageArray[i][j][2]));

                int rgb = (red << 16) | (green << 8) | blue;
                image.setRGB(j, i, rgb);
            }
        }

        ImageIO.write(image, "bmp", new File(outputPath));
    }

    public static void resizeBmpFile(String inputPath, String outputPath, int newHeight, int newWidth) throws IOException {
        int[][][] originalImage = loadBmpToArray(inputPath);

        int[][][] resizedImage = resize(originalImage, newHeight, newWidth);

        saveArrayToBmp(resizedImage, outputPath);
    }
}
