# This file defines the Solanum::Lang::RootContext class.


require 'solanum/monitor/matcher'


module Solanum
module Lang

# This class provides the execution context for input sources.
#
# Author:: Greg Look
class InputContext
    
    # Creates a new RootContext
    def initialize(source)
        @source = source
    end
    
    # Matches a single line of the input
    def match(pattern, options={}, &block)
        raise "pattern must be provided" if pattern.nil?
        
        commands = 0
        commands += 1 if options[:record]
        commands += 1 if block_given?
        raise "Must specify :record or provide a block to execute" if commands == 0
        raise "Only one of :record or a block should be provided" if commands > 1
        
        if options[:record]
            block = lambda do |m|
                op = options[:as] || :to_s
                value = m[1].send op
                record options[:record], value, options[:unit]
            end
        end
        
        @source.matchers << Solanum::Monitor::Matcher.new(pattern, &block)
    end
end

end
end
