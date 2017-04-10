# Database_System
# Project: Efficient Stream Processing
This project concerns about network monitoring. Providing a stream of IP src and dest addr, I used sketches and sliding window algorithms to improve performance of stream analytics.


# Task1: Efficient Jumping Window Structure
Using Jumping Window to count the frequency.

Details:

Implement an event-based jumping window structure that enables frequency queries over the stream. Using lazy updating, and supporting the following queries:

a. Given a source IP x.y.z.w, the structure should return the number of packets that have been sent from any IP address x.*.*.* in the last W network packets, with a maximum absolute error ε*W. For example, given IP 142.212.132.4, the structure should estimate how many packets have been sent from the IPs 142.*.*.* in the last W events. Values of W and ε will be given as parameters at construction time.

b. Given a source IP x.y.z.w and a query window size 𝑊 ≤ 𝑊, return the number of 1
packets sent from the IP x.*.*.* in the last 𝑊 events.


# Task 2: Frequency queries with memory constraints
Using sketches(CM and Bloom Filter) to return frequencies.

Details:

You are working at an Internet Service Provider that handles a network of at most 400.000 IP addresses. You want to keep an approximate count of the number of packets sent by any IP address from your network. You are given a value Z, which denotes the maximum available memory that you are allowed to use, in bytes. Create the necessary data structure by combining any of the sketches seen in the class such that:

i) Any frequency query for an item that never appears in the stream will return 0, with a false positive probability at most pr1 , which is given by the user at construction time.

ii) Any frequency query for an item that appears in the stream will return an estimate for the frequency with a maximum absolute error ε*L, with a probability pr2. L is the number of items seen so far. ε and pr2 will be given by the user at construction time.

The structure should fit in the available space. Divide the space among the data structures you choose, such that the accuracy requirements are satisfied. If this is not possible, e.g., not enough space is allowed to satisfy the requirements, then the constructor should throw an exception.


# Task 3: Range containment queries
Design a structure based on Bloom filters that can answer range containment queries

Details:

You are working at an Internet Service Provider that handles a network of at most 400.000 IP addresses. You are asked to design a structure based on Bloom filters that can answer range containment queries, i.e., given a range [l,r] of IP addresses from your network, the structure will return true if the packets seen so far contain at least one IP address x, with l <= x <=r, with a false positive probability at most pr.

Constraint: You are allowed to execute at most 64 queries on your structure per user query, independently of the length of the query range.
