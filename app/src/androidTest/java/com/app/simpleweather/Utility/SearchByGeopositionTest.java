package com.app.simpleweather.Utility;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SearchByGeopositionTest {
    private SearchByGeoposition subj;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        subj = mock(SearchByGeoposition.class);
    }

    @Test
    public void doInBackground() throws JSONException {
        doCallRealMethod().when(subj).doInBackground("asdf");
        when(subj.getLatitude()).thenReturn("60.338935");
        when(subj.getLongitude()).thenReturn("102.296999");

        String result = subj.doInBackground("asdf");
        JSONObject jsonObject = new JSONObject(result);

        assertThat(jsonObject.get("results").toString(), is("[{\"bounds\":{\"northeast\":{\"lat\":60.340111,\"lng\":102.3016197}," +
                "\"southwest\":{\"lat\":60.3385137,\"lng\":102.2881436}},\"components\":{\"ISO_3166-1_alpha-2\":\"RU\"," +
                "\"ISO_3166-1_alpha-3\":\"RUS\",\"_type\":\"road\",\"city\":\"сельское поселение Село Ванавара\"," +
                "\"continent\":\"Asia\",\"country\":\"Russia\",\"country_code\":\"ru\",\"county\":\"Evenkiysky Rayon\"," +
                "\"road\":\"Садовая улица\",\"road_type\":\"residential\",\"state\":\"Krasnoyarsk Krai\"},\"confidence\":9," +
                "\"formatted\":\"Садовая улица, сельское поселение Село Ванавара, Krasnoyarsk Krai, Russia\",\"geometry\":{\"lat\":60.3398229,\"lng\":102.296885}}]"));
        assertThat(jsonObject.get("status").toString(), is("{\"code\":200,\"message\":\"OK\"}"));
    }


    @Test
    public void onPostExecute() {
    }

    @Test
    public void helloKitty() {
        doCallRealMethod().when(subj).printHelloKitty(anyBoolean());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        System.setOut(ps);

        subj.printHelloKitty(false);



        assertEquals(baos.toString(),"Hello Kitty\n");




        try {
            subj.printHelloKitty(true);
            fail();
        } catch (NullPointerException e) {

        }
    }
}
