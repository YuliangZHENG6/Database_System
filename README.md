# Database_System
# Project: Efficient Stream Processing
This project concerns about network monitoring. Providing a stream of IP src and dest addr, I used sketches and sliding window algorithms to improve performance of stream analytics.


# Task1: Efficient Jumping Window Structure
Implement an event-based jumping window structure that enables frequency queries over the stream. Using lazy updating, and supporting the following queries:
a. Given a source IP x.y.z.w, the structure should return the number of packets that have been sent from any IP address x.*.*.* in the last W network packets, with a maximum absolute error ε*W. For example, given IP 142.212.132.4, the structure should estimate how many packets have been sent from the IPs 142.*.*.* in the last W events. Values of W and ε will be given as parameters at construction time.
b. Given a source IP x.y.z.w and a query window size 𝑊 ≤ 𝑊, return the number of 1
packets sent from the IP x.*.*.* in the last 𝑊 events.


# Task 2: Frequency queries with memory constraints
Using sketches(CM and Bloom Filter) to return frequencies.

# Task 3: Range containment queries
Design a structure based on Bloom filters that can answer range containment queries
