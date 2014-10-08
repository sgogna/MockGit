package formatters;

import helpers.FileReader;

import java.io.File;

public abstract class AbstractTextFormatter
{
    String xmlSuffix = ".xml";

    public abstract String getLeftLabel(File filepath1, File filepath2);

    public abstract String getRightLabel(File filepath1, File filepath2);

    public final String chooseFile = "Choose file to open";
    Boolean fixedWidthForLabels = false;
    int fixedWidth = 8;

    public String openFormate(File filepath)
    {
        if (filepath == null)
            return "No file to show";
        String content = FileReader.readFile(filepath);

        if (filepath.getName().endsWith(xmlSuffix))
        {
            return formateXML(content);
        }
        return formate(content);
    }

    String formateXML(String source)
    {

        return XmlFormatter.format(source);
    }

    String formate(String source)
    {

        return source;
    }

    String reformat(String toFormat)
    {
        String text = toFormat;

        text = text.replaceAll("<", "&lt;");
        text = text.replaceAll(">", "&gt;");
        text = text.replaceAll("\n", "<\\BR>");
        text = text.replaceAll(" ", "&nbsp;");
        text = text.replaceAll("\"", "&rdquo;");
        return text;
    }

}
