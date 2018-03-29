package com.thingworx.sdk.steam;

import org.joda.time.DateTime;

import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.metadata.FieldDefinition;
import com.thingworx.metadata.annotations.ThingworxEventDefinition;
import com.thingworx.metadata.annotations.ThingworxEventDefinitions;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinitions;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.metadata.collections.FieldDefinitionCollection;
import com.thingworx.types.BaseTypes;
import com.thingworx.types.InfoTable;
import com.thingworx.types.collections.ValueCollection;
import com.thingworx.types.constants.CommonPropertyNames;
import com.thingworx.types.primitives.StringPrimitive;

// Refer to the "Steam Sensor Example" section of the documentation
// for a detailed explanation of this example's operation

// Property Definitions
@SuppressWarnings("serial")
@ThingworxPropertyDefinitions(properties = {
        @ThingworxPropertyDefinition(name = "Temperature", description = "Current Temperature",
                baseType = "NUMBER")
//        ,
//        @ThingworxPropertyDefinition(name = "Pressure", description = "Current Pressure",
//                baseType = "NUMBER", category = "Status", aspects = { "isReadOnly:true" }),
//        @ThingworxPropertyDefinition(name = "FaultStatus", description = "Fault status",
//                baseType = "BOOLEAN", category = "Faults", aspects = { "isReadOnly:true" }),
//        @ThingworxPropertyDefinition(name = "InletValve", description = "Inlet valve state",
//                baseType = "BOOLEAN", category = "Status", aspects = { "isReadOnly:true" }),
//        @ThingworxPropertyDefinition(name = "TemperatureLimit",
//                description = "Temperature fault limit", baseType = "NUMBER", category = "Faults",
//                aspects = { "isReadOnly:false" }),
//        @ThingworxPropertyDefinition(name = "TotalFlow", description = "Total flow",
//                baseType = "NUMBER", category = "Aggregates", aspects = { "isReadOnly:true" }),
})


// Event Definitions
@ThingworxEventDefinitions(events = { @ThingworxEventDefinition(name = "SteamSensorFault",
        description = "Steam sensor fault", dataShape = "SteamSensor.Fault", category = "Faults",
        isInvocable = true, isPropertyEvent = false) })

// Steam Thing virtual thing class that simulates a Steam Sensor
public class SteamThing extends VirtualThing implements Runnable {

    private double _totalFlow = 0.0;
    private Thread _shutdownThread = null;
    private int counter = 0;

    private final static String TEMPERATURE_FIELD = "OutsideTemperature";
    private final static String SENSOR_NAME_FIELD = "SensorName";
    private final static String ACTIV_TIME_FIELD = "ActivationTime";
    private final static String PRESSURE_FIELD = "BarometricPressure";
    private final static String FAULT_STATUS_FIELD = "CurrentFaultStatus";
    private final static String INLET_VALVE_FIELD = "CurrentInletValve";
    private final static String TEMPERATURE_LIMIT_FIELD = "RatedTemperatureLimit";
    private final static String TOTAL_FLOW_FIELD = "TotalFlowAmount";


    public SteamThing(String name, String description, String identifier,
            ConnectedThingClient client) throws Exception {
        super(name, description, identifier, client);

        // Data Shape definition that is used by the steam sensor fault event
        // The event only has one field, the message
        FieldDefinitionCollection faultFields = new FieldDefinitionCollection();
        faultFields.addFieldDefinition(
                new FieldDefinition(CommonPropertyNames.PROP_MESSAGE, BaseTypes.STRING));
        defineDataShapeDefinition("SteamSensor.Fault", faultFields);

        // Populate the thing shape with the properties, services, and events that are annotated in
        // this code
        super.initializeFromAnnotations();
        this.init();
    }

    // From the VirtualThing class
    // This method will get called when a connect or reconnect happens
    // Need to send the values when this happens
    // This is more important for a solution that does not send its properties on a regular basis
    public void synchronizeState() {
        // Be sure to call the base class
        super.synchronizeState();
        // Send the property values to Thingworx when a synchronization is required
        super.syncProperties();
    }

    private void init() throws Exception {
//        initializeFromAnnotations();

//        FieldDefinitionCollection fields = new FieldDefinitionCollection();
//        fields.addFieldDefinition(new FieldDefinition(SENSOR_NAME_FIELD, BaseTypes.STRING));
//        fields.addFieldDefinition(new FieldDefinition(ACTIV_TIME_FIELD, BaseTypes.DATETIME));
//        fields.addFieldDefinition(new FieldDefinition(TEMPERATURE_FIELD, BaseTypes.NUMBER));
//        fields.addFieldDefinition(new FieldDefinition(PRESSURE_FIELD, BaseTypes.NUMBER));
//        fields.addFieldDefinition(new FieldDefinition(FAULT_STATUS_FIELD, BaseTypes.BOOLEAN));
//        fields.addFieldDefinition(new FieldDefinition(INLET_VALVE_FIELD, BaseTypes.BOOLEAN));
//        fields.addFieldDefinition(new FieldDefinition(TEMPERATURE_LIMIT_FIELD, BaseTypes.NUMBER));
//        fields.addFieldDefinition(new FieldDefinition(TOTAL_FLOW_FIELD, BaseTypes.INTEGER));
//        defineDataShapeDefinition("SteamSensorReadings", fields);
    }

//    @ThingworxServiceDefinition(name = "GetSteamSensorReadings",
//            description = "Get SteamSensor Readings")
//    @ThingworxServiceResult(name = CommonPropertyNames.PROP_RESULT, description = "Result",
//            baseType = "INFOTABLE", aspects = { "dataShape:SteamSensorReadings" })
//    public InfoTable GetSteamSensorReadings() {
//        InfoTable table = new InfoTable(getDataShapeDefinition("SteamSensorReadings"));
//
//        ValueCollection entry = new ValueCollection();
//
//        DateTime now = DateTime.now();
//
//        try {
//            // entry 1
//            entry.clear();
//            entry.SetStringValue(SENSOR_NAME_FIELD, "Sensor Alpha");
//            entry.SetDateTimeValue(ACTIV_TIME_FIELD, now.plusDays(1));
//            entry.SetNumberValue(TEMPERATURE_FIELD, 50);
//            entry.SetNumberValue(PRESSURE_FIELD, 15);
//            entry.SetBooleanValue(FAULT_STATUS_FIELD, false);
//            entry.SetBooleanValue(INLET_VALVE_FIELD, true);
//            entry.SetNumberValue(TEMPERATURE_LIMIT_FIELD, 150);
//            entry.SetNumberValue(TOTAL_FLOW_FIELD, 87);
//            table.addRow(entry.clone());
//
//            // entry 2
//            entry.clear();
//            entry.SetStringValue(SENSOR_NAME_FIELD, "Sensor Beta");
//            entry.SetDateTimeValue(ACTIV_TIME_FIELD, now.plusDays(2));
//            entry.SetNumberValue(TEMPERATURE_FIELD, 60);
//            entry.SetNumberValue(PRESSURE_FIELD, 25);
//            entry.SetBooleanValue(FAULT_STATUS_FIELD, true);
//            entry.SetBooleanValue(INLET_VALVE_FIELD, true);
//            entry.SetNumberValue(TEMPERATURE_LIMIT_FIELD, 150);
//            entry.SetNumberValue(TOTAL_FLOW_FIELD, 77);
//            table.addRow(entry.clone());
//
//            // entry 3
//            entry.clear();
//            entry.SetStringValue(SENSOR_NAME_FIELD, "Sensor Gamma");
//            entry.SetDateTimeValue(ACTIV_TIME_FIELD, now.plusDays(3));
//            entry.SetNumberValue(TEMPERATURE_FIELD, 70);
//            entry.SetNumberValue(PRESSURE_FIELD, 30);
//            entry.SetBooleanValue(FAULT_STATUS_FIELD, true);
//            entry.SetBooleanValue(INLET_VALVE_FIELD, true);
//            entry.SetNumberValue(TEMPERATURE_LIMIT_FIELD, 150);
//            entry.SetNumberValue(TOTAL_FLOW_FIELD, 67);
//            table.addRow(entry.clone());
//
//            // entry 4
//            entry.clear();
//            entry.SetStringValue(SENSOR_NAME_FIELD, "Sensor Delta");
//            entry.SetDateTimeValue(ACTIV_TIME_FIELD, now.plusDays(4));
//            entry.SetNumberValue(TEMPERATURE_FIELD, 80);
//            entry.SetNumberValue(PRESSURE_FIELD, 35);
//            entry.SetBooleanValue(FAULT_STATUS_FIELD, false);
//            entry.SetBooleanValue(INLET_VALVE_FIELD, true);
//            entry.SetNumberValue(TEMPERATURE_LIMIT_FIELD, 150);
//            entry.SetNumberValue(TOTAL_FLOW_FIELD, 57);
//            table.addRow(entry.clone());
//
//            // entry 5
//            entry.clear();
//            entry.SetStringValue(SENSOR_NAME_FIELD, "Sensor Epsilon");
//            entry.SetDateTimeValue(ACTIV_TIME_FIELD, now.plusDays(5));
//            entry.SetNumberValue(TEMPERATURE_FIELD, 90);
//            entry.SetNumberValue(PRESSURE_FIELD, 40);
//            entry.SetBooleanValue(FAULT_STATUS_FIELD, true);
//            entry.SetBooleanValue(INLET_VALVE_FIELD, false);
//            entry.SetNumberValue(TEMPERATURE_LIMIT_FIELD, 150);
//            entry.SetNumberValue(TOTAL_FLOW_FIELD, 47);
//            table.addRow(entry.clone());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return table;
//    }

    // The processScanRequest is called by the SteamSensorClient every scan cycle
    @Override
    public void processScanRequest() throws Exception {
        // Be sure to call the base classes scan request
        super.processScanRequest();
        // Execute the code for this simulation every scan
        this.scanDevice();
    }

    // Performs the logic for the steam sensor, occurs every scan cycle
    public void scanDevice() throws Exception {
        ++counter;

        if ((counter % 1) == 0) {
            // Set the Temperature property value in the range of 400-440
            double temperature = 400 + 40 * Math.random();
            super.setProperty("Temperature", temperature);

            // Get the TemperatureLimmit property value from memory
//            double temperatureLimit =
//                    (Double) getProperty("TemperatureLimit").getValue().getValue();

            // Set the FaultStatus property value if the TemperatureLimit value is exceeded
            // and it is greater than zero
            boolean faultStatus = false;

//            if (temperatureLimit > 0 && temperature > temperatureLimit)
                faultStatus = true;

            // If the sensor has a fault...
            if (faultStatus) {
                // Get the previous value of the fault from the property
                // This is the current value because it hasn't been set yet
                // This is done because we don't want to send the event every time it enters the
                // fault state,
                // only send the fault on the transition from non-faulted to faulted
//                boolean previousFaultStatus =
//                        (Boolean) getProperty("FaultStatus").getValue().getValue();

                // If the current value is not faulted, then create and queue the event
//                if (!previousFaultStatus) {
//                    // Set the event information of the defined data shape for the event
//                    ValueCollection eventInfo = new ValueCollection();
//                    eventInfo.put(CommonPropertyNames.PROP_MESSAGE,
//                            new StringPrimitive("Temperature at " + temperature
//                                    + " was above limit of " + temperatureLimit));
//                    // Queue the event
//                    super.queueEvent("SteamSensorFault", DateTime.now(), eventInfo);
//                }
            }

            // Set the fault status property value
//            super.setProperty("FaultStatus", faultStatus);


        }

//        if ((counter % 2) == 0) {
//            // Set the Pressure property value in the range of 18-23
//            double pressure = 18 + 5 * Math.random();
//            // Set the property values
//            super.setProperty("Pressure", pressure);
//        }

//        if ((counter % 3) == 0) {
//            // Add a random double value from 0.0-1.0 to the total flow
//            this._totalFlow += Math.random();
//
//            // Set the InletValve property value to true by default
//            boolean inletValveStatus = true;
//
//
//            counter = 0;
//        }

        // Update the subscribed properties and events to send any updates to Thingworx
        // Without calling these methods, the property and event updates will not be sent
        // The numbers are timeouts in milliseconds.
        super.updateSubscribedProperties(15000);
        super.updateSubscribedEvents(60000);
    }

//    @ThingworxServiceDefinition(name = "AddNumbers", description = "Add Two Numbers")
//    @ThingworxServiceResult(name = CommonPropertyNames.PROP_RESULT, description = "Result",
//            baseType = "NUMBER")
//    public Double AddNumbers(
//            @ThingworxServiceParameter(name = "a", description = "Value 1",
//                    baseType = "NUMBER") Double a,
//            @ThingworxServiceParameter(name = "b", description = "Value 2",
//                    baseType = "NUMBER") Double b)
//            throws Exception {
//
//        return a + b;
//    }

//    @ThingworxServiceDefinition(name = "GetBigString", description = "Get big string")
//    @ThingworxServiceResult(name = CommonPropertyNames.PROP_RESULT, description = "Result",
//            baseType = "STRING")
//    public String GetBigString() {
//        StringBuilder sbValue = new StringBuilder();
//
//        for (int i = 0; i < 24000; i++) {
//            sbValue.append('0');
//        }
//
//        return sbValue.toString();
//    }

//    @ThingworxServiceDefinition(name = "Shutdown", description = "Shutdown the client")
//    @ThingworxServiceResult(name = CommonPropertyNames.PROP_RESULT, description = "",
//            baseType = "NOTHING")
//    public synchronized void Shutdown() throws Exception {
//        // Should not have to do this, but guard against this method being called more than once.
//        if (this._shutdownThread == null) {
//            // Create a thread for shutting down and start the thread
//            this._shutdownThread = new Thread(this);
//            this._shutdownThread.start();
//        }
//    }

    @Override
    public void run() {
        try {
            // Delay for a period to verify that the Shutdown service will return
            Thread.sleep(1000);
            // Shutdown the client
            this.getClient().shutdown();
        } catch (Exception x) {
            // Not much can be done if there is an exception here
            // In the case of production code should at least log the error
        }
    }
}
