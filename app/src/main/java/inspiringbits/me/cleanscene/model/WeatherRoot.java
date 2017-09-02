package inspiringbits.me.cleanscene.model;

import java.util.List;

/**
 * Created by Ivan on 2017/9/2.
 */

public class WeatherRoot {
    private String cod;
    private int message;
    private int cnt;
    private List<WeatherList> list;

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }



    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public List<WeatherList> getList() {
        return list;
    }

    public void setList(List<WeatherList> list) {
        this.list = list;
    }
}
