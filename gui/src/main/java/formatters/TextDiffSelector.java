/* Copyright 2009 EB2 International Limited */
package formatters;

import java.util.LinkedList;

import name.fraser.neil.plaintext.diff_match_patch;
import name.fraser.neil.plaintext.diff_match_patch.Diff;
import name.fraser.neil.plaintext.diff_match_patch.Operation;
import util.Pair;
import constants.DiffStyleConstants;

public class TextDiffSelector
{
public static Pair< String> selectDiff(String text1, String text2){
        StringBuilder builder1 = new StringBuilder(DiffStyleConstants.HIGHLIGHT_STYLE);
        StringBuilder builder2 = new StringBuilder(DiffStyleConstants.HIGHLIGHT_STYLE);
        diff_match_patch diffMatchPatch = new diff_match_patch();
        LinkedList<Diff> list = diffMatchPatch.diff_main(text1, text2);
        for (Diff diff : list)
        {
            if (diff.operation == Operation.DELETE)
            {
                builder1.append(DiffStyleConstants.BEGIN_FORMAT);
                builder1.append(diff.text);
                builder1.append(DiffStyleConstants.END_FORMAT);
            }
            else if (diff.operation == Operation.INSERT)
            {
                builder2.append(DiffStyleConstants.BEGIN_FORMAT);
                builder2.append(diff.text);
                builder2.append(DiffStyleConstants.END_FORMAT);
            }
            else
            {
                builder1.append(diff.text);
                builder2.append(diff.text);
            }
        }
        return new Pair<String>(builder1.toString(), builder2.toString());
}
}
