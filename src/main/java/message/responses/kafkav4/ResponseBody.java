package message.responses.kafkav4;

public class ResponseBody {
    short error_code ;
    int arrayLength ; // array size + 1
    short apiKey;
    short minVersion ;
    short maxVersion ;
    byte array_tagged_fields ;
    int throttle_time ;
    byte outer_tagged_fields ;

    ResponseBody(){}

    // Getters & Setters :
    public short getError_code() {
        return error_code;
    }

    public void setError_code(short error_code) {
        this.error_code = error_code;
    }

    public int getArrayLength() {
        return arrayLength;
    }

    public void setArrayLength(int arrayLength) {
        this.arrayLength = arrayLength;
    }

    public short getApiKey() {
        return apiKey;
    }

    public void setApiKey(short apiKey) {
        this.apiKey = apiKey;
    }

    public short getMinVersion() {
        return minVersion;
    }

    public void setMinVersion(short minVersion) {
        this.minVersion = minVersion;
    }

    public short getMaxVersion() {
        return maxVersion;
    }

    public void setMaxVersion(short maxVersion) {
        this.maxVersion = maxVersion;
    }

    public byte getArray_tagged_fields(){
        return this.array_tagged_fields ;
    }
    public void setArray_tagged_fields(byte array_tagged_fields) {
        this.array_tagged_fields = array_tagged_fields;
    }

    public int getThrottle_time() {
        return throttle_time;
    }
    public void setThrottle_time(int throttle_time) {
        this.throttle_time = throttle_time;
    }

    public byte getOuter_tagged_fields(){
        return this.outer_tagged_fields ;
    }
    public void setOuter_tagged_fields(byte outer_tagged_fields) {
        this.outer_tagged_fields = outer_tagged_fields;
    }
}