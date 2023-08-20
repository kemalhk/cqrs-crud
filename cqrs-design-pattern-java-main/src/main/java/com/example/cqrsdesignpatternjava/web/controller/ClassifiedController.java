package com.example.cqrsdesignpatternjava.web.controller;

import com.example.cqrsdesignpatternjava.core.cq.command.impl.classified.CreateClassifiedCommand;
import com.example.cqrsdesignpatternjava.core.cq.command.impl.classified.CreateClassifiedCommandHandler;
import com.example.cqrsdesignpatternjava.core.cq.command.impl.classified.DeleteClassifiedCommand;
import com.example.cqrsdesignpatternjava.core.cq.command.impl.classified.DeleteClassifiedCommandHandler;
import com.example.cqrsdesignpatternjava.core.cq.command.result.CommandResult;
import com.example.cqrsdesignpatternjava.core.cq.query.impl.classified.GetClassifiedsQuery;
import com.example.cqrsdesignpatternjava.core.cq.query.impl.classified.GetClassifiedsQueryHandler;
import com.example.cqrsdesignpatternjava.entity.Classified;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/classifieds")
public class ClassifiedController {

    private final GetClassifiedsQueryHandler getClassifiedsQueryHandler;
    private final CreateClassifiedCommandHandler createClassifiedCommandHandler;
    private final DeleteClassifiedCommandHandler deleteClassifiedCommandHandler;

    public ClassifiedController(GetClassifiedsQueryHandler getClassifiedsQueryHandler, CreateClassifiedCommandHandler createClassifiedCommandHandler, DeleteClassifiedCommandHandler deleteClassifiedCommandHandler) {
        this.getClassifiedsQueryHandler = getClassifiedsQueryHandler;
        this.createClassifiedCommandHandler = createClassifiedCommandHandler;
        this.deleteClassifiedCommandHandler = deleteClassifiedCommandHandler;
    }

    @GetMapping
    public ResponseEntity<?> index() throws Exception {
        List<Classified> classifieds = this.getClassifiedsQueryHandler.handle(new GetClassifiedsQuery());
        return ResponseEntity.ok(classifieds);
    }

    @PostMapping
    public CommandResult add(@RequestBody CreateClassifiedCommand command) throws IOException, TimeoutException {
        return this.createClassifiedCommandHandler.handle(command);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClassified(@PathVariable Long id){
        DeleteClassifiedCommand deleteCommand = new DeleteClassifiedCommand();
        deleteCommand.setClassifiedId(id);
        deleteClassifiedCommandHandler.handle(deleteCommand);
        return ResponseEntity.ok("silme işlemi");
    }
}