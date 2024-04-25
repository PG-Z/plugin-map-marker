package top.aiheiyo.map.service.task;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.aiheiyo.map.service.IMapService;

/**
 * Description: task
 *
 * @author : evan  Date: 2024/4/25
 */
@RequiredArgsConstructor
@Component
public class AutoExecuteTask {

    private final IMapService mapService;

    @Scheduled(cron = "0 0 * * * *")
    public void sync() {
        mapService.syncMapSpec();
    }
}
