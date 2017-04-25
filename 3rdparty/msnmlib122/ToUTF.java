import java.io.*;

public class ToUTF
{
    public ToUTF()
    {

    }

    public void recursive( File dir ) throws IOException
    {
        File[] files = dir.listFiles();
        for(int i=0; i<files.length; i++)
        {
            if( files[i].isDirectory() )
            {
                recursive( files[i] );
            }
            else
            if( files[i].isFile() && files[i].getName().endsWith(".java") )
            {
                translate( files[i] );
            }
        }
    }

    public static void translate( File f ) throws IOException
    {
        byte[] b = new byte[ (int)f.length() ];
        FileInputStream fis = new FileInputStream(f);
        int off = 0;
        while( off < b.length )
        {
            int readlen = fis.read( b, off, b.length-off );
            if( readlen==-1 )
                break;
            readlen += off;
        }
        fis.close();

        byte[] n = new String(b, "EUC-KR").getBytes("UTF-8");
        FileOutputStream fos = new FileOutputStream(f);
        fos.write( n );
        fos.flush();
        fos.close();
    }

    public static void main( String[] args ) throws Exception
    {
        translate( new File(args[0]) );
    }
}
