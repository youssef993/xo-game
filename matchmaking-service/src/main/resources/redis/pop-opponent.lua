local queueKey = KEYS[1]
local ticketPrefix = ARGV[1]
local currentPlayerId = ARGV[2]

while true do
    local opponentId = redis.call('LPOP', queueKey)

    if not opponentId then
        return nil
    end

    if opponentId ~= currentPlayerId then
        local ticketKey = ticketPrefix .. opponentId
        local ticketJson = redis.call('GET', ticketKey)

        if ticketJson then
            return opponentId
        end
    end
end