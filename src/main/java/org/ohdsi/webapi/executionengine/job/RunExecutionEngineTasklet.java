package org.ohdsi.webapi.executionengine.job;

import org.ohdsi.webapi.executionengine.dto.ExecutionRequestDTO;
import org.ohdsi.webapi.executionengine.service.ScriptExecutionService;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import static org.ohdsi.webapi.executionengine.job.CreateAnalysisTasklet.ANALYSIS_EXECUTION_ID;

public class RunExecutionEngineTasklet extends BaseExecutionTasklet {

    public static final String SCRIPT_ID = "scriptId";
    private final ScriptExecutionService executionService;
    private final ExecutionRequestDTO executionRequest;

    public RunExecutionEngineTasklet(ScriptExecutionService executionService, ExecutionRequestDTO executionRequest) {

        this.executionService = executionService;
        this.executionRequest = executionRequest;
    }
    
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

        final int analysisExecutionId = getInt(ANALYSIS_EXECUTION_ID);
        put(SCRIPT_ID, executionService.runScript(executionRequest, analysisExecutionId));
        return RepeatStatus.FINISHED;
    }
}
