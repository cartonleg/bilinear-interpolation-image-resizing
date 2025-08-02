import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {
            BufferedImage image = ImageIO.read(new File("images/gray-image.bmp"));
            int height = image.getHeight();
            int width = image.getWidth();
            BilinearResizing.resizeBmpFile("images/gray-image.bmp", "images/gray-image-size-down.bmp", "bmp", height/2, width/2);
            BilinearResizing.resizeBmpFile("images/gray-image-size-down.bmp",  "images/gray-image-size-up.bmp", "bmp", height*2, width*2);
        }
        catch (Exception e) {
            System.err.println("Error resizing bmp file.");
        }
    }
}