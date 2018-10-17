package org.ohdsi.webapi.user.importer.service;

import org.ohdsi.analysis.Utils;
import org.ohdsi.webapi.Constants;
import org.ohdsi.webapi.user.importer.model.AtlasUserRoles;
import org.ohdsi.webapi.user.importer.model.LdapProviderType;
import org.ohdsi.webapi.user.importer.model.RoleGroupMapping;
import org.ohdsi.webapi.user.importer.model.UserImportResult;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserImportTasklet extends BaseUserImportTasklet<UserImportResult> implements StepExecutionListener {

  private List<AtlasUserRoles> users;

  private UserImportResult result;

  public UserImportTasklet(TransactionTemplate transactionTemplate, UserImportService userImportService) {

    super(LoggerFactory.getLogger(UserImportTasklet.class), transactionTemplate, userImportService);
  }

  @Override
  protected UserImportResult doUserImportTask(ChunkContext chunkContext, LdapProviderType providerType, Boolean preserveRoles, RoleGroupMapping roleGroupMapping) {

    if (Objects.isNull(users)) {
      Map<String, Object> jobParameters = chunkContext.getStepContext().getJobParameters();
      String userRolesJson = (String) jobParameters.get(Constants.Params.USER_ROLES);
      if (Objects.isNull(userRolesJson)) {
        throw new IllegalArgumentException("userRoles is required for user import task");
      }
      users = Utils.deserialize(userRolesJson, factory -> factory.constructCollectionType(List.class, AtlasUserRoles.class));
    }
    return result = userImportService.importUsers(users, preserveRoles);
  }

  @Override
  protected void doAfter(StepContribution stepContribution, ChunkContext chunkContext) {
    if (Objects.nonNull(result)) {
      stepContribution.setExitStatus(new ExitStatus(ExitStatus.COMPLETED.getExitCode(),
              String.format("Created %d new users, updated %d users", result.getCreated(), result.getUpdated())));
    }
  }

  @Override
  public void beforeStep(StepExecution stepExecution) {

    String userRolesJson = stepExecution.getJobExecution().getExecutionContext().getString(Constants.Params.USER_ROLES);
    if (Objects.nonNull(userRolesJson)){
      users = Utils.deserialize(userRolesJson, factory -> factory.constructCollectionType(List.class, AtlasUserRoles.class));
    }
  }

  @Override
  public ExitStatus afterStep(StepExecution stepExecution) {
    return null;
  }
}
