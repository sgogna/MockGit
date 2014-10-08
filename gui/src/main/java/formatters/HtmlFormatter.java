/* Copyright 2009 EB2 International Limited */
package formatters;

public class HtmlFormatter
{
public static String format(String text)
{
    text = text.replaceAll("<", "&lt;");
    text = text.replaceAll(">", "&gt;");
    text = text.replaceAll("\n", "<\\BR>");
    text = text.replaceAll(" ", "&nbsp;");
    text = text.replaceAll("\"", "&rdquo;");
    return text;
}
}
