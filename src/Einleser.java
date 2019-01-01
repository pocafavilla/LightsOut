import java.awt.image.BufferedImage;
import java.io.IOException;
 
import javax.imageio.ImageIO;
 
 public class Einleser {
 
        public String pfad;
        public int breite;
        public int hoehe;
 
        public int[] pixel;
 
        public Einleser(String pfad) {
                BufferedImage bild = null;
               
                try {
                	bild = ImageIO.read(Einleser.class.getResourceAsStream(pfad));
                } catch (IOException e) {
                        e.printStackTrace();
                }
               
                this.pfad = pfad;
                this.breite = bild.getWidth();
                this.hoehe = bild.getHeight();
               
                pixel = bild.getRGB(0, 0, breite, hoehe, null, 0, breite);

         }

   }