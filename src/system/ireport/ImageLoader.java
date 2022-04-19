/*
 * Program je s��as�ou syst�mu EaSys V2
 * 
 * EaSys je volne ��rite�n� k�d.
 */

package system.ireport;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

/**
 *
 * @author rvanya
 */
public class ImageLoader {
public ImageLoader()
{
}

public static final java.awt.Image loadImage(String fileName)
{
try
{
URL url = ClassLoader.getSystemResource(fileName);
//if (url == null) return null;
return Toolkit.getDefaultToolkit().getImage(url);
}
catch (Exception e)
{
return null;
}
}
} 