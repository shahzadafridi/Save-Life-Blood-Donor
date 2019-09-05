package official.com.savelife_blooddonor.Network;

import official.com.savelife_blooddonor.Network.Model.MyPlaces;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleAPIService {

    @GET
    Call<MyPlaces> getNearByPlaces(@Url String url);


}
