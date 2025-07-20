package com.weyland.bishop.controller;

import com.weyland.core.command.CommandDto;
import com.weyland.core.command.CommandService;
import com.weyland.bishop.service.BishopInternalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/android")
public class AndroidController {

    private final CommandService commandService;
    private final BishopInternalService internalService;

    public AndroidController(CommandService commandService, BishopInternalService internalService) {
        this.commandService = commandService;
        this.internalService = internalService;
    }

    @PostMapping("/command")
    public ResponseEntity<String> receiveCommand(@Valid @RequestBody CommandDto command) {
        internalService.runDiagnostics(command.author());
        commandService.executeCommand(command);
        
        return ResponseEntity.ok("Команда '" + command.description() + "' принята к исполнению.");
    }
}