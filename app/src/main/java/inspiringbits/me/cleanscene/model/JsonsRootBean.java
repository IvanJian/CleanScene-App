package inspiringbits.me.cleanscene.model;
import java.util.List;

/**
 * Auto-generated: 2017-09-02 21:36:57
 *
 * @author www.jsons.cn 
 * @website http://www.jsons.cn/json2java/ 
 */
public class JsonsRootBean {

    private String cod;
    private int message;
    private City city;
    private int cnt;
    private List<WList> list;
    public void setCod(String cod) {
         this.cod = cod;
     }
     public String getCod() {
         return cod;
     }

    public void setMessage(int message) {
         this.message = message;
     }
     public int getMessage() {
         return message;
     }

    public void setCity(City city) {
         this.city = city;
     }
     public City getCity() {
         return city;
     }

    public void setCnt(int cnt) {
         this.cnt = cnt;
     }
     public int getCnt() {
         return cnt;
     }

    public List<WList> getList() {
        return list;
    }

    public void setList(List<WList> list) {
        this.list = list;
    }
}