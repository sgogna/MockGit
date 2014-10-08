package status;

public class StatusCommunicationRequest
{
    private boolean correct;
    private String failureReason;
    private String comparator;
    private boolean flowDiffFailsTest;

    public String getComparator()
    {
        return comparator;
    }

    public void setComparator(String comparator)
    {
        this.comparator = comparator;
    }

    public boolean isFlowDiffFailsTest()
    {
        return flowDiffFailsTest;
    }

    public void setFlowDiffFailsTest(boolean flowDiffFailsTest)
    {
        this.flowDiffFailsTest = flowDiffFailsTest;
    }

    public String getFailureReason()
    {
        return failureReason;
    }

    public void setFailureReason(String failureReason)
    {
        this.failureReason = failureReason;
    }

    public boolean isCorrect()
    {
        return correct;
    }

    public void setCorrect(boolean correct)
    {
        this.correct = correct;
    }
}
