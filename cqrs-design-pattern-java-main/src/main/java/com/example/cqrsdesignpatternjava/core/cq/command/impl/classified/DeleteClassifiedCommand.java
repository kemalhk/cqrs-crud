package com.example.cqrsdesignpatternjava.core.cq.command.impl.classified;

import com.example.cqrsdesignpatternjava.core.cq.command.Command;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteClassifiedCommand implements Command, Serializable {
    private Long classifiedId;
}
