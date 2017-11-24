package Classification;

public class ResultData {

    public String pictureType, resultOfKnn;
    public boolean result = false;

    ResultData(String p, String r) {
        pictureType = p;
        resultOfKnn = r;

        if (pictureType.equals(resultOfKnn)) {
            result = true;
        }
    }
}
