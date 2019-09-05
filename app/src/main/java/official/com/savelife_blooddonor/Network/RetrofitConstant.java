package official.com.savelife_blooddonor.Network;

public class RetrofitConstant {
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";
    private String TAG = "Common";

    public static IGoogleAPIService getGoogleAPIService(){

        return RetrofitClient.getclient(GOOGLE_API_URL).create(IGoogleAPIService.class);

    }
}
