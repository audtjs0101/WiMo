package com.example.wimo.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wimo.Map.TMap;
import com.example.wimo.R;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPOIItem;

public class MapFragment extends Fragment {
    private LinearLayout linearLayoutTmap; // TMap 지도로 바꿔줄 레이아웃

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_map, container, false);

        linearLayoutTmap = viewGroup.findViewById(R.id.linearLayoutTmap); // TMap 지도로 바꿔줄 레이아웃의 아이디 할당

        // 예외처리인데,MapFragment는 HistoryActivity.java 에서 address 값을 전달받는다고 작성했었습니다. 실제로 getArguments()에 담겨있는건데, 만약 null이면 빌드가 실패하도록 하라는 뜻입니다.
        assert getArguments() != null;

        // HistoryActivity.java 에서 전달받은 address값을 변수에 저장
        String address = getArguments().getString("address");

        // TMap.java를 새로 만들었습니다. 이 객체가 tMap을 담당할것입니다.
        TMap tMap = new TMap(getContext(), linearLayoutTmap);

        // Tmap 데이터 초기화
        TMapData tmapdata = new TMapData();

        // Tmap에 Address 값을 주고, 검색을 명령합니다.
        tmapdata.findAllPOI(address, poiItem -> {

            /**
             * runOnUiThread : UI(화면을 그리는)구성을 하는 스레드를 하나 만들어서, 지도를 그리도록 합니다.
             * 왜 UI스레드를 쓰는가 ?
             * 검색한 결과를 받는 시간과, 실제로 그 맵을 그리는 시간에 대한 오차가 발생할수 있습니다.
             * 이럴때, 잠시 기다리는 작업이 필요한데, 안드로이드 OS의 특성상 단 1초도 기다리는순간, 전체 프로세스가 마비됩니다.
             *
             * 그렇기 때문에 따로 UI를 담당하는 스레드를 만들어서, 검색결과는 검색 결과대로받고, 지도는 따로 동시에 그리도록 설계함이 있습니다.
             */

            getActivity().runOnUiThread(() -> {

                // 만약 검색결과가 0개 이상이라면
                if (poiItem.size() > 0) {
                    TMapPOIItem tMapPOIItem = poiItem.get(0); // 검색결과는 가장 첫번째꺼(가장 근사치에 속하는 주소)

                    // 그 주소 검색결과의 위도 경도를 받아와서 저장
                    Double lat = Double.parseDouble(tMapPOIItem.getPOIPoint().toString().split(" ")[1]);
                    Double lon = Double.parseDouble(tMapPOIItem.getPOIPoint().toString().split(" ")[3]);

                    // 주소, 위도, 경도를 보내서 Map을 그립니다.
                    tMap.createTMap(address, lat, lon);
                }
                else{
                    Toast.makeText(getContext(), "일치하는 주소가 없습니다.",Toast.LENGTH_SHORT).show();
                }
            });
        });

        return viewGroup;
    }
}
