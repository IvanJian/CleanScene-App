package inspiringbits.me.cleanscene.model;

import java.util.List;

/**
 * Auto-generated: 2017-09-02 21:36:57
 *
 * @author www.jsons.cn 
 * @website http://www.jsons.cn/json2java/ 
 */
public class WList {

    private int dt;
    private Temp temp;
    private double pressure;
    private int humidity;
    private List<Weather> weather;
    private double speed;
    private int deg;
    private int clouds;
    public void setDt(int dt) {
         this.dt = dt;
     }
     public int getDt() {
         return dt;
     }

    public void setTemp(Temp temp) {
         this.temp = temp;
     }
     public Temp getTemp() {
         return temp;
     }

    public void setPressure(double pressure) {
         this.pressure = pressure;
     }
     public double getPressure() {
         return pressure;
     }

    public void setHumidity(int humidity) {
         this.humidity = humidity;
     }
     public int getHumidity() {
         return humidity;
     }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public void setSpeed(double speed) {
         this.speed = speed;
     }
     public double getSpeed() {
         return speed;
     }

    public void setDeg(int deg) {
         this.deg = deg;
     }
     public int getDeg() {
         return deg;
     }

    public void setClouds(int clouds) {
         this.clouds = clouds;
     }
     public int getClouds() {
         return clouds;
     }

}