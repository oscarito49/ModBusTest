package mx.gigabyte.modbus;

import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;

public class Prueba01 {
    public static void main(String[] args) {
        System.out.println("Bennuti");
//        ModbusTcpMasterConfig config = new ModbusTcpMasterConfig.Builder("localhost").build();
//        ModbusTcpMaster master = new ModbusTcpMaster(config);
//
//        master.connect();
//
//        CompletableFuture<ReadHoldingRegistersResponse> future =
//                master.sendRequest(new ReadHoldingRegistersRequest(0, 10), 0);
//
//        future.thenAccept(response -> {
//            System.out.println("Response: " + ByteBufUtil.hexDump(response.getRegisters()));
//
//            ReferenceCountUtil.release(response);
//        });


        System.out.println("-------------------------------------------------");

        String connectionString = "modbus://localhost";

        try (PlcConnection plcConnection = new PlcDriverManager().getConnection(connectionString)) {

            // Check if this connection support reading of data.
            if (!plcConnection.getMetadata().canRead()) {
                System.out.println("This connection doesn't support reading.");
                return;
            }

            // Create a new read request:
            // - Give the single item requested the alias name "value"
            PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();
            builder.addItem("value-1", "coil:1");
            builder.addItem("value-2", "coil:3[4]");
            builder.addItem("value-3", "coil:3");
            builder.addItem("value-4HR", "holding-register:1");
            builder.addItem("value-5HR", "holding-register:3[6]");
            PlcReadRequest readRequest = builder.build();

            PlcReadResponse response = readRequest.execute().get();

//            response.getResponseCode();

            for (String fieldName : response.getFieldNames()) {
                if(response.getResponseCode(fieldName) == PlcResponseCode.OK) {
                    int numValues = response.getNumberOfValues(fieldName);
                    // If it's just one element, output just one single line.
                    if(numValues == 1) {
                        System.out.println("Value[" + fieldName + "]: " + response.getObject(fieldName));
                    }
                    // If it's more than one element, output each in a single row.
                    else {
                        System.out.println("Value[" + fieldName + "]:");
                        for(int i = 0; i < numValues; i++) {
                            System.out.println(" - " + response.getObject(fieldName, i));
                        }
                    }
                }
                // Something went wrong, to output an error message instead.
                else {
                    System.out.println("Error[" + fieldName + "]: " + response.getResponseCode(fieldName).name());
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
