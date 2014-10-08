package formatters;

import java.util.LinkedList;

import name.fraser.neil.plaintext.diff_match_patch.Diff;
import name.fraser.neil.plaintext.diff_match_patch.Operation;
import constants.DiffStyleConstants;

public class DiffHighlighter 
{

    public static String getHighlightedText(LinkedList<Diff> diff, Operation operation)
    {
        String output = "";

        for (Diff i : diff)
        {
            String text = (i.text);
            if (i.operation == Operation.EQUAL)
            {
                output += text;
            }
            if (i.operation == operation)
            {
                output += DiffStyleConstants.BEGIN_FORMAT + text + DiffStyleConstants.END_FORMAT;
            }
        }
        return output;
    }


}
