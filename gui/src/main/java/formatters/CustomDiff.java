package formatters;

import fileManagers.MainFileManager;
import filehandlers.ProfileManager;
import helpers.FileReader;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;
import name.fraser.neil.plaintext.diff_match_patch.Operation;
import spring.SpringBeanContainer;

import com.sabre.ssw.proxy.compare.profile.Profile;
import com.sabre.ssw.proxy.compare.profile.TagRecord;

import constants.DiffStyleConstants;

public class CustomDiff
{
    private static final Pattern NAMESPACE_PATTERN_1 = Pattern.compile("&lt;/?[^:&]*" + DiffStyleConstants.END_FORMAT + "[^:&]*:");
    private static final Pattern NAMESPACE_PATTERN_2 = Pattern.compile("xmlns:[^=]+=&rdquo;[^&]*" + DiffStyleConstants.END_FORMAT + "[^&]*&rdquo;");

    public static String getDiffText(File file1, File file2)
    {
        if (file1 == null)
        {
            return "";
        }
        if (file2 == null)
        {
            return HtmlFormatter.format(XmlFormatter.format(FileReader.readFile(file1)));
        }
        ProfileManager profileManager = SpringBeanContainer.getProfileManagerBean();
        Profile profile = profileManager.getActiveProfile();
        if (profile != null && !profile.isContentCompare())
        {
            return DiffStyleConstants.HIGHLIGHT_STYLE + HtmlFormatter.format(XmlFormatter.format(FileReader.readFile(file1)));
        }
        return DiffStyleConstants.HIGHLIGHT_STYLE + removeFormatting(DiffHighlighter.getHighlightedText(getDiffList(file1, file2), Operation.DELETE), file1, file2);
    }

    private static LinkedList<Diff> getDiffList(File file1, File file2)
    {
        diff_match_patch x = new diff_match_patch();
        LinkedList<Diff> list = x.diff_main(HtmlFormatter.format(XmlFormatter.format(FileReader.readFile(file1))), HtmlFormatter.format(XmlFormatter.format(FileReader.readFile(file2))));
        return list;
    }

    private static String removeDiff(final Pattern pattern, String text)
    {
        Matcher matcher = pattern.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find())
        {
            String searchResult = matcher.group();
            searchResult = searchResult.replaceAll(DiffStyleConstants.BEGIN_FORMAT_REGEX, "").replaceAll(DiffStyleConstants.END_FORMAT, "");
            matcher.appendReplacement(buffer, searchResult);
        }
        matcher.appendTail(buffer);
        text = buffer.toString();
        return text;
    }

    private static String removeFormatting(String text, File path, File path1)
    {
        ProfileManager profileManager = SpringBeanContainer.getProfileManagerBean();
        Profile p = profileManager.getProfile(profileManager.getActiveProfileName());
        if (p == null)
            return text;
        if (p.isSkipNameSpace())
        {
            text = removeDiff(NAMESPACE_PATTERN_1, text);
            text = removeDiff(NAMESPACE_PATTERN_2, text);
        }
        String file = MainFileManager.getFile(path);
        for (TagRecord t : p.getTagList())
        {
            if (checkFileMatches(t, file))
            {
                String tag = t.getTagCheckBoxValue() ? t.getTag() : t.getTag().replaceAll("\\?", ".?").replaceAll("\\*", ".*?");
                String attribute = t.getAttributeCheckBoxValue() ? t.getAttribute() : t.getAttribute().replaceAll("\\?", ".?").replaceAll("\\*", ".*?");

                if (attribute.equals(".*?"))
                {
                    Pattern pattern;
                    if (tag.equals(".*?"))
                    {
                        text = text.replaceAll(DiffStyleConstants.BEGIN_FORMAT_REGEX, "").replaceAll(DiffStyleConstants.END_FORMAT, "");
                    }
                    else
                    {
                        if (p.isSkipNameSpace())
                        {
                            if (tag.indexOf(":") > 0)
                            {
                                tag = tag.substring(tag.indexOf(":") + 1);
                            }
                            tag = "([^:&/]+:)?" + tag;
                        }
                        tag = tag.replaceAll("\\.", "[a-zA-Z0-9]");
                        pattern = Pattern.compile("&lt;([^/]?" + tag + ")(&nbsp;|&gt;)");
                        Matcher tagMatcher = pattern.matcher(text);
                        List<String> tags = new ArrayList<String>();
                        while (tagMatcher.find())
                        {
                            tags.add(tagMatcher.group(1));
                        }
                        for (String fullTag : tags)
                        {
                            pattern = Pattern.compile("&lt;" + fullTag + "(((.(?!&gt;))*?/&gt;)|(.*?&lt;/" + fullTag + "&gt;))");
                            text = removeDiff(pattern, text);
                        }
                    }
                }
                else
                {
                    if (tag.equals(".*?"))
                    {
                        text = replaceArument(text, attribute);
                    }
                    else
                    {
                        if (p.isSkipNameSpace())
                        {
                            if (tag.indexOf(":") > 0)
                            {
                                tag = tag.substring(tag.indexOf(":") + 1);
                            }
                            tag = "([^:&]+:)?" + tag;
                        }
                        tag = tag.replaceAll("\\.", "[a-zA-Z0-9]");
                        Pattern pattern = Pattern.compile("&lt;" + tag + "&nbsp;.*?&gt;");
                        Matcher m = pattern.matcher(text);
                        StringBuffer sb = new StringBuffer();
                        while (m.find())
                        {
                            String txt = text.substring(m.start(), m.end());
                            txt = replaceArument(txt, attribute);
                            m.appendReplacement(sb, txt);
                        }
                        m.appendTail(sb);
                        text = sb.toString();
                    }
                }
            }
        }
        return text;
    }

    private static String replaceArument(String text, String argument)
    {
        argument = argument.replaceAll("\\.", "[^=&]");
        Pattern pattern = Pattern.compile("&nbsp;" + argument + "=&rdquo;[^&]*" + DiffStyleConstants.END_FORMAT + "[^&]*&rdquo;");
        text = removeDiff(pattern, text);
        return text;
    }

    private static boolean checkFileMatches(TagRecord record, String file)
    {
        String value = record.getFileCheckBoxValue() ? record.getFile() : record.getFile().replaceAll("\\?", ".?").replaceAll("\\*", ".*?");
        if (!value.equals(".*?"))
        {
            value = record.isCommand() ? ".*?" + value : value;
        }
        return (value.equals(".*?") || file.matches(value));
    }

    public boolean isDiff(File file1, File file2)
    {
        return CustomDiff.getDiffText(file1, file2).contains(DiffStyleConstants.END_FORMAT);
    }

}
