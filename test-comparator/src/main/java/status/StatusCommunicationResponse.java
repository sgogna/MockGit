package status;

import java.util.LinkedList;
import java.util.List;

public class StatusCommunicationResponse
{
    private boolean correct;
    private List<String> differences = new LinkedList<String>();

    public boolean isCorrect()
    {
        return correct;
    }

    public void setCorrect(boolean correct)
    {
        this.correct = correct;
    }

    public List<String> getDiffFiles()
    {
        return differences;
    }

    public void setDiffFiles(List<String> list)
    {
        this.differences = list;
    }
}
