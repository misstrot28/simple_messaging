//
//  ContentView.swift
//  SimpleMessaging
//
//  Created by Nirson Niño Samson on 7/16/24.
//

import SwiftUI

struct ContentView: View {
    @State private var serverIpAddress = ""
    @State private var server: Server?
        @State private var client: Client?
        @State private var serverIp: String = ""
        @State private var message: String = ""
    
    var body: some View {
        VStack {
                    Button("Start Server") {
                        server = Server()
                        server?.start()
                    }

                    TextField("Server IP Address", text: $serverIp)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding()

                    Button("Connect as Client") {
                        client = Client()
                        client?.connect(to: serverIp)
                    }

                    TextField("Message", text: $message)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .padding()

                    Button("Send Message") {
                        client?.sendMessage(message)
                    }
                }
                .padding()
    }
}

#Preview {
    ContentView()
}
