# DomoticzApp Architecture Diagrams

This document provides visual representations of the DomoticzApp architecture using Mermaid diagrams.

## System Architecture

The following diagram shows the overall architecture of the DomoticzApp ecosystem:

```mermaid
flowchart LR
    subgraph "Mobile Device"
        App[DomoticzApp\nAndroid Application]
    end
    
    subgraph "Server Infrastructure"
        Server[DomoticzAppServer\nMiddleware]
        MQTT[MQTT Broker]
        Domoticz[Domoticz\nHome Automation]
        Cameras[IP Cameras]
    end
    
    App <--WebSocket--> Server
    Server <--MQTT--> MQTT
    MQTT <--MQTT--> Domoticz
    Cameras --> Server
    Server --> App
    
    style App fill:#f9f,stroke:#333,stroke-width:2px
    style Server fill:#bbf,stroke:#333,stroke-width:2px
    style MQTT fill:#bfb,stroke:#333,stroke-width:2px
    style Domoticz fill:#fbb,stroke:#333,stroke-width:2px
    style Cameras fill:#fbf,stroke:#333,stroke-width:2px
```

## App Component Structure

The following diagram shows the main components of the DomoticzApp:

```mermaid
classDiagram
    class MainActivity {
        +onCreate()
        +onServiceCreatedCallback()
        +setupUIComponents()
    }
    
    class MainModelView {
        +initializeControllers()
        +setupMessageHandlerCallbacks()
        +setupUIComponents()
        +getAlertController()
        +getCameraController()
        +getGateController()
    }
    
    class DomoticzAppService {
        +onBind()
        +getMessageHandler()
        +startLocationUpdates()
    }
    
    class AppServerConnector {
        +initializeConnection()
        +sendMessage()
        +isConnected()
    }
    
    class MessageHandler {
        +handleMessage()
        +processNotification()
        +processCameraList()
    }
    
    class AlertController {
        +addAlert()
        +purgeAlerts()
        +setAlertView()
    }
    
    class CameraController {
        +loadCamera()
        +nextCamera()
        +previousCamera()
    }
    
    class GateController {
        +openGate()
    }
    
    class GeofenceController {
        +startMonitoring()
        +stopMonitoring()
        +onGeofenceEnter()
        +onGeofenceExit()
    }
    
    class AppPreferences {
        +getServerIpAddress()
        +getServerPort()
        +getGeofenceEnabled()
        +getGeofenceRadius()
        +getWebSocketUrl()
    }
    
    MainActivity --> MainModelView
    MainActivity --> DomoticzAppService
    DomoticzAppService --> MessageHandler
    DomoticzAppService --> AppServerConnector
    DomoticzAppService --> GeofenceController
    MainModelView --> AlertController
    MainModelView --> CameraController
    MainModelView --> GateController
    MainModelView --> GeofenceController
    AppServerConnector --> AppPreferences
    GeofenceController --> AppPreferences
    MessageHandler --> AlertController
    MessageHandler --> CameraController
```

## Communication Flow

The following sequence diagram shows the communication flow between the app, server, and Domoticz:

```mermaid
sequenceDiagram
    participant User
    participant App as DomoticzApp
    participant Server as DomoticzAppServer
    participant MQTT as MQTT Broker
    participant Domoticz
    
    Note over App,Server: Connection Establishment
    App->>Server: WebSocket Connection Request
    Server->>App: Connection Established
    
    Note over Server,MQTT: MQTT Subscription
    Server->>MQTT: Subscribe to domoticz/out/#
    MQTT->>Server: Subscription Confirmed
    
    Note over User,App: User Interaction
    User->>App: Tap "Open Gate" Button
    App->>Server: Send "opengate" Message
    Server->>MQTT: Publish to domoticz/in/gate
    MQTT->>Domoticz: Forward Gate Command
    Domoticz->>MQTT: Publish Gate Status
    MQTT->>Server: Forward Gate Status
    Server->>App: Send Confirmation
    
    Note over Domoticz,App: Event Notification
    Domoticz->>MQTT: Publish Event (e.g., Doorbell)
    MQTT->>Server: Forward Event
    Server->>App: Send Notification
    App->>User: Display Alert
    
    Note over App,Server: Geofence Trigger
    App->>Server: Send Geofence Enter Event
    Server->>MQTT: Publish to Custom Topic
    MQTT->>Domoticz: Forward Geofence Event
    
    Note over App,Server: Camera Request
    App->>Server: Request Camera List
    Server->>App: Send Camera URLs
    App->>User: Display Camera Feed
```

## Data Flow Diagram

The following diagram shows the data flow within the DomoticzApp ecosystem:

```mermaid
flowchart TD
    User[User] --> |Interacts with| UI[User Interface]
    UI --> |Sends commands| Controllers[Controllers]
    Controllers --> |Uses| Connectors[Network Connectors]
    Connectors --> |Communicates with| Server[DomoticzAppServer]
    Server --> |Sends/Receives| MQTT[MQTT Broker]
    MQTT --> |Communicates with| Domoticz[Domoticz System]
    Domoticz --> |Controls| Devices[Smart Home Devices]
    
    Preferences[App Preferences] --> |Configures| Controllers
    Preferences --> |Configures| Connectors
    
    Server --> |Streams| Cameras[IP Cameras]
    Cameras --> |Provides feeds to| UI
    
    Domoticz --> |Triggers events| MQTT
    MQTT --> |Forwards events| Server
    Server --> |Sends notifications| Connectors
    Connectors --> |Updates| Controllers
    Controllers --> |Updates| UI
    UI --> |Notifies| User
    
    Location[Location Services] --> |Provides location to| GeofenceController[Geofence Controller]
    GeofenceController --> |Triggers based on location| Connectors
    
    subgraph "Mobile Device"
        UI
        Controllers
        Connectors
        Preferences
        Location
        GeofenceController
    end
    
    subgraph "Server Infrastructure"
        Server
        MQTT
        Domoticz
        Cameras
        Devices
    end
```

## State Diagram for Geofence

The following state diagram shows the states and transitions for the geofence functionality:

```mermaid
stateDiagram-v2
    [*] --> Disabled: App Start (Geofence Disabled)
    Disabled --> Enabled: User Enables Geofence
    Enabled --> Disabled: User Disables Geofence
    
    state Enabled {
        [*] --> Outside: Initial State
        Outside --> Approaching: Enter Monitoring Zone
        Approaching --> Inside: Sufficient Measurements
        Inside --> Outside: Exit Geofence
        
        state Approaching {
            [*] --> Measurement1: First Detection
            Measurement1 --> Measurement2: Continue in Zone
            Measurement2 --> Measurement3: Continue in Zone
            Measurement3 --> [*]: Threshold Reached
        }
    }
    
    Inside --> TriggerSent: Send Trigger to Server
    TriggerSent --> Inside: Wait for Exit
    
    Disabled --> [*]: App Close
    Enabled --> [*]: App Close
```

## Component Interaction Diagram

The following diagram shows how the different components of the app interact:

```mermaid
flowchart TB
    subgraph UI[User Interface Layer]
        MainActivity
        SettingsActivity
    end
    
    subgraph VM[ViewModel Layer]
        MainModelView
    end
    
    subgraph Controllers[Controller Layer]
        AlertController
        CameraController
        GateController
        GeofenceController
        NotificationController
        ServerIconController
    end
    
    subgraph Services[Service Layer]
        DomoticzAppService
        BroadcastReceiver
        MessageHandler
    end
    
    subgraph Connectivity[Connectivity Layer]
        AppServerConnector
        LocationConnector
    end
    
    subgraph Model[Model Layer]
        AppPreferences
        Geofence
    end
    
    MainActivity --> MainModelView
    MainActivity --> DomoticzAppService
    SettingsActivity --> AppPreferences
    
    MainModelView --> AlertController
    MainModelView --> CameraController
    MainModelView --> GateController
    MainModelView --> GeofenceController
    MainModelView --> NotificationController
    MainModelView --> ServerIconController
    
    DomoticzAppService --> MessageHandler
    DomoticzAppService --> BroadcastReceiver
    
    Controllers <--> Services
    
    Services --> Connectivity
    
    Connectivity --> Model
    Controllers --> Model
    
    style UI fill:#f9f,stroke:#333,stroke-width:2px
    style VM fill:#bbf,stroke:#333,stroke-width:2px
    style Controllers fill:#bfb,stroke:#333,stroke-width:2px
    style Services fill:#fbb,stroke:#333,stroke-width:2px
    style Connectivity fill:#fbf,stroke:#333,stroke-width:2px
    style Model fill:#ff9,stroke:#333,stroke-width:2px
```

## Server Architecture

The following diagram shows the architecture of the DomoticzAppServer:

```mermaid
flowchart TB
    subgraph Main[Main Application]
        main.py
    end
    
    subgraph Connectivity[Connectivity Layer]
        domoticzAppAPI[domoticzAppAPI.py]
        mqttConnection[mqttConnection.py]
        cameraConnection[cameraConnection.py]
    end
    
    subgraph Controller[Controller Layer]
        alertHandler[alertHandler.py]
        appMessageHandler[appMessageHandler.py]
        cameraHandler[cameraHandler.py]
        gateStateHandler[gateStateHandler.py]
        mqttMessageHandler[mqttMessageHandler.py]
    end
    
    subgraph Model[Model Layer]
        alertQueue[alertQueue.py]
        messageFilter[messageFilter.py]
    end
    
    subgraph Utils[Utilities]
        logger[logger.py]
    end
    
    main.py --> Connectivity
    main.py --> Controller
    
    domoticzAppAPI --> appMessageHandler
    mqttConnection --> mqttMessageHandler
    cameraConnection --> cameraHandler
    
    appMessageHandler --> alertHandler
    appMessageHandler --> gateStateHandler
    appMessageHandler --> cameraHandler
    
    mqttMessageHandler --> alertHandler
    mqttMessageHandler --> gateStateHandler
    
    alertHandler --> alertQueue
    mqttMessageHandler --> messageFilter
    
    Controller --> Utils
    Connectivity --> Utils
    
    style Main fill:#f9f,stroke:#333,stroke-width:2px
    style Connectivity fill:#bbf,stroke:#333,stroke-width:2px
    style Controller fill:#bfb,stroke:#333,stroke-width:2px
    style Model fill:#fbb,stroke:#333,stroke-width:2px
    style Utils fill:#fbf,stroke:#333,stroke-width:2px
